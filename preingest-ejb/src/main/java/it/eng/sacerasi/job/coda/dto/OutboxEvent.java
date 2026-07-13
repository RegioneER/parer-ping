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

package it.eng.sacerasi.job.coda.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.generic.GenericRecord;

/**
 * Wrapper tipizzato su {@link GenericRecord} prodotto da {@code KafkaAvroDeserializer} (Confluent)
 * leggendo la tabella {@code PIG_OUTBOX_EVENT} tramite JDBC Source Connector.
 * <p>
 * Evita l'accesso diretto a stringhe magiche nei consumer e centralizza la deserializzazione del
 * campo {@code PAYLOAD} (JSON). Non richiede alcuna configurazione aggiuntiva sul connector
 * (nessuna SMT {@code SetSchemaMetadata}, nessun {@code connect.meta.data}).
 */
public class OutboxEvent {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final GenericRecord avroRecord;

    public OutboxEvent(GenericRecord avroRecord) {
        this.avroRecord = avroRecord;
    }

    /**
     * PK di {@code PIG_OUTBOX_EVENT} — usata per il controllo di idempotenza.
     *
     * @return id outbox event o {@code null} se assente
     */
    public Long getIdOutboxEvent() {
        Object v = avroRecord.get("ID_OUTBOX_EVENT");
        return v != null ? (Long) v : null;
    }

    /**
     * UUID dell'evento outbox (colonna {@code ID}).
     *
     * @return UUID stringa o {@code null} se assente
     */
    public String getId() {
        Object v = avroRecord.get("ID");
        return v != null ? v.toString() : null;
    }

    /**
     * Tipo aggregato (es. nome del topic di destinazione prima del routing).
     *
     * @return aggregate type o {@code null} se assente
     */
    public String getAggregateType() {
        Object v = avroRecord.get("AGGREGATETYPE");
        return v != null ? v.toString() : null;
    }

    /**
     * ID dell'aggregato (usato come chiave del messaggio Kafka).
     *
     * @return aggregate id o {@code null} se assente
     */
    public String getAggregateId() {
        Object v = avroRecord.get("AGGREGATEID");
        return v != null ? v.toString() : null;
    }

    /**
     * Tipo evento (discriminante business).
     *
     * @return type o {@code null} se assente
     */
    public String getType() {
        Object v = avroRecord.get("TYPE");
        return v != null ? v.toString() : null;
    }

    /**
     * Payload grezzo in formato JSON.
     *
     * @return payload JSON o {@code null} se assente
     */
    public String getPayload() {
        Object v = avroRecord.get("PAYLOAD");
        return v != null ? v.toString() : null;
    }

    /**
     * Timestamp di creazione in millisecondi (epoch).
     *
     * @return timestamp epoch ms o {@code null} se assente
     */
    public Long getCreatedAt() {
        Object v = avroRecord.get("CREATED_AT");
        return v != null ? (Long) v : null;
    }

    /**
     * Deserializza il campo {@code PAYLOAD} (JSON) nel tipo Java indicato.
     *
     * @param targetType classe target
     * @param <T>        tipo di ritorno
     * @return istanza di {@code targetType} deserializzata dal payload JSON
     * @throws JsonProcessingException se il JSON non e' valido o non mappabile
     */
    public <T> T getDeserializedPayload(Class<T> targetType) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(getPayload(), targetType);
    }
}
