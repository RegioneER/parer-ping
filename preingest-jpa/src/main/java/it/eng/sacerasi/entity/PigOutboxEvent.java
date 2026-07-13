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

package it.eng.sacerasi.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "PIG_OUTBOX_EVENT")
public class PigOutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_OUTBOX_EVENT", nullable = false)
    private Long idOutboxEvent;

    @Column(name = "ID", nullable = false, length = 36, updatable = false)
    private String id;

    @Column(name = "ID_INCREMENT", nullable = false, insertable = false, updatable = false)
    private Long idIncrement;

    @Column(name = "AGGREGATETYPE", nullable = false, length = 255)
    private String aggregateType;

    @Column(name = "AGGREGATEID", nullable = false, length = 255)
    private String aggregateId;

    @Column(name = "TYPE", nullable = false, length = 255)
    private String type;

    @Lob
    @Column(name = "PAYLOAD", nullable = false)
    private String payload;

    @Column(name = "CREATED_AT", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "PROCESSED", nullable = false)
    private Integer processed;

    @Column(name = "PROCESSED_AT")
    private LocalDateTime processedAt;

    // Lifecycle callbacks

    @PrePersist
    protected void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString();
        }
        if (this.processed == null) {
            this.processed = 0;
        }
    }

    // Getters e Setters

    public Long getIdOutboxEvent() {
        return idOutboxEvent;
    }

    public void setIdOutboxEvent(Long idOutboxEvent) {
        this.idOutboxEvent = idOutboxEvent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getIdIncrement() {
        return idIncrement;
    }

    public void setIdIncrement(Long idIncrement) {
        this.idIncrement = idIncrement;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getProcessed() {
        return processed;
    }

    public void setProcessed(Integer processed) {
        this.processed = processed;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
}
