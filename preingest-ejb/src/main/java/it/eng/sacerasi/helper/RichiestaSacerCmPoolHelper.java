// Java
package it.eng.sacerasi.helper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.net.ssl.SSLContext;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.sacerasi.common.Constants;
import it.eng.sacerasi.web.helper.ConfigurationHelper;

@Singleton(mappedName = "RichiestaSacerCmPoolHelper")
@Startup
public class RichiestaSacerCmPoolHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RichiestaSacerCmPoolHelper.class);

    private static final int DEF_MAX_TOTAL = 200;
    private static final int DEF_MAX_PER_ROUTE = 20;
    @EJB
    private ConfigurationHelper configurationHelper;

    private PoolingHttpClientConnectionManager connectionManager;

    @PostConstruct
    public void init() {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(null, (chain, authType) -> true).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create().register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory()).build();
            connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } catch (Exception ex) {
            connectionManager = new PoolingHttpClientConnectionManager();
        }
        // from configuration
        connectionManager.setMaxTotal(getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(getMaxPerRoute());

        LOGGER.debug(
                "Connection manager initialized with max total connections: {}, max per route: {}",
                connectionManager.getMaxTotal(), connectionManager.getDefaultMaxPerRoute());
    }

    @PreDestroy
    public void destroy() {
        if (connectionManager != null) {
            connectionManager.shutdown();

            LOGGER.debug("Connection manager shut down.");
        }
    }

    public CloseableHttpClient createHttpClient(RequestConfig config) {
        return HttpClients.custom().setDefaultRequestConfig(config).setConnectionManagerShared(true) // avoid
                // close
                // connetion
                // manager
                // when
                // closing
                // client
                .setConnectionManager(connectionManager).build();
    }

    // Method to get the maximum total connections from configuration
    private Integer getMaxTotal() {
        try {
            String poolMaxVersStr = configurationHelper
                    .getValoreParamApplicByApplic(Constants.POOL_MAX_VERS_SACER);
            return poolMaxVersStr != null ? Integer.parseInt(poolMaxVersStr) : DEF_MAX_TOTAL;
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid configuration for maximum total connections", e);
            return DEF_MAX_TOTAL;
        }
    }

    // Method to get the maximum connections per route from configuration
    private Integer getMaxPerRoute() {
        try {
            String poolMaxPerRouteStr = configurationHelper
                    .getValoreParamApplicByApplic(Constants.POOL_MAX_PER_ROUTE_VERS_SACER);
            return poolMaxPerRouteStr != null ? Integer.parseInt(poolMaxPerRouteStr)
                    : DEF_MAX_PER_ROUTE;
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid configuration for maximum connections per route", e);
            return DEF_MAX_PER_ROUTE;
        }
    }
}
