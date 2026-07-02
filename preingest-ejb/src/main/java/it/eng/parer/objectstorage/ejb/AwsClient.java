/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */
package it.eng.parer.objectstorage.ejb;

import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.web.helper.ConfigurationHelper;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.checksums.RequestChecksumCalculation;
import software.amazon.awssdk.core.checksums.ResponseChecksumValidation;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import it.eng.sacerasi.common.Constants;

/**
 * Cache lock-free per i client AWS S3.
 *
 * <p>
 * La creazione di un {@link S3Client} è computazionalmente pesante (setup del connection pool
 * Apache HttpClient). Tenendo i client in cache tramite {@link ConcurrentHashMap} con
 * {@link ConcurrencyManagementType#BEAN} si evita il lock a livello EJB Container e si ottiene
 * accesso completamente lock-free dopo la prima inizializzazione.
 * </p>
 *
 * @author Snidero_L
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class AwsClient {

    private final Logger log = LoggerFactory.getLogger(AwsClient.class);

    private final Map<CacheKey, S3Client> clientCache = new ConcurrentHashMap<>();

    @EJB
    protected ConfigurationHelper configurationHelper;

    private S3Client lookupClient(URI storageAddress, String accessKeyId, String secretKey) {

        final CacheKey key = new CacheKey(storageAddress, accessKeyId, secretKey);

        S3Client client = clientCache.computeIfAbsent(key,
                k -> createS3Client(storageAddress, accessKeyId, secretKey));
        log.debug("Sto per effettuare il collegamento all'endpoint S3 [{}] dal thread {}",
                storageAddress, Thread.currentThread().getName());
        return client;

    }

    /*
     * Nota: con l'introduzione di
     * https://docs.aws.amazon.com/AmazonS3/latest/userguide/VirtualHosting.html è necessario
     * abilitare "forzatamente" la modalità path style (attenzione: in futuro sarà deprecata!)
     */
    private S3Client createS3Client(URI storageAddress, String accessKeyId, String secretKey) {

        final AwsCredentialsProvider credProvider = StaticCredentialsProvider
                .create(AwsBasicCredentials.create(accessKeyId, secretKey));

        return S3Client.builder().endpointOverride(storageAddress).region(Region.US_EAST_1)
                .credentialsProvider(credProvider).forcePathStyle(true)
                .responseChecksumValidation(ResponseChecksumValidation.WHEN_REQUIRED)
                .requestChecksumCalculation(RequestChecksumCalculation.WHEN_REQUIRED)
                .httpClientBuilder(ApacheHttpClient.builder().maxConnections(maxConnections())
                        .connectionTimeout(connectionTimeoutOfMinutes())
                        .socketTimeout(socketTimeoutOfMinutes()).tcpKeepAlive(true))
                .build();
    }

    /**
     * Ottiene il numero di connessioni massimo per client. Se parametro non impostato corettamente,
     * viene utilizzato un default di 100
     *
     * @return numero di connessioni
     *
     */
    private final Integer maxConnections() {
        final String longParameterString = configurationHelper
                .getValoreParamApplicByApplic(Constants.S3_CLIENT_MAX_CONNECTIONS);
        return NumberUtils.isDigits(longParameterString) ? Integer.valueOf(longParameterString)
                : 100; // default
    }

    /**
     * Ottiene il timeout di connessione espresso in minuti. Se parametro non impostato
     * corettamente, viene utilizzato un default di 1 minuto
     *
     * @return minuti
     *
     */
    private final Duration connectionTimeoutOfMinutes() {
        final String longParameterString = configurationHelper
                .getValoreParamApplicByApplic(Constants.S3_CLIENT_CONNECTION_TIMEOUT);
        return Duration.ofMinutes(
                NumberUtils.isDigits(longParameterString) ? Long.valueOf(longParameterString) : 1L); // default
    }

    /**
     * Ottiene il timeout di socket espresso in minuti. Se parametro non impostato corettamente,
     * viene utilizzato un default di 10 minuti
     *
     * @return minuti
     *
     */
    private final Duration socketTimeoutOfMinutes() {
        final String longParameterString = configurationHelper
                .getValoreParamApplicByApplic(Constants.S3_CLIENT_SOCKET_TIMEOUT);
        return Duration.ofMinutes(
                NumberUtils.isDigits(longParameterString) ? Long.valueOf(longParameterString)
                        : 10L); // default
    }

    /**
     * Ottieni il client (possibilmente in cache) per il collegamento a questo OS
     *
     * @param storageAddress URI dell'object storage
     * @param accessKeyId    accessKeyID per l'accesso a questo bucket
     * @param secretKey      secretKe per l'accesso a questo bucket
     *
     * @return client S3 configurato
     */
    public S3Client getClient(URI storageAddress, String accessKeyId, String secretKey) {
        return lookupClient(storageAddress, accessKeyId, secretKey);
    }

    @PreDestroy
    private void clear() {
        for (S3Client s3Client : clientCache.values()) {
            if (s3Client != null) {
                s3Client.close();
            }
        }
    }

    private static final class CacheKey {
        private final URI storageAddress;
        private final String accessKeyId;
        private final String secretKey;

        CacheKey(URI storageAddress, String accessKeyId, String secretKey) {
            this.storageAddress = storageAddress;
            this.accessKeyId = accessKeyId;
            this.secretKey = secretKey;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof CacheKey))
                return false;
            CacheKey that = (CacheKey) o;
            return java.util.Objects.equals(storageAddress, that.storageAddress)
                    && java.util.Objects.equals(accessKeyId, that.accessKeyId)
                    && java.util.Objects.equals(secretKey, that.secretKey);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(storageAddress, accessKeyId, secretKey);
        }
    }

}
