package com.helpdesk.document;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;


@CompoundIndex(
    name = "ticket_createdAt_idx",
    def = "{'ticketId': 1, 'createdAt': 1}"
)

@CompoundIndex(
    name = "unique_ticket_warning_idx",
    def = "{'ticketId': 1, 'action': 1}",
    unique = true,
    partialFilter = "{'action': 'WARNING'}"
)

@Document(collection = "activities")
public class ActivityDocument {

    @Id
    private String id;

    private Long ticketId;
    private Long actorId;
    private String action;
    private String details;
    private LocalDateTime createdAt;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Long getTicketId() {
        return ticketId;
    }
    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
    public Long getActorId() {
        return actorId;
    }
    public void setActorId(Long actorId) {
        this.actorId = actorId;
    }
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getDetails() {
        return details;
    }
    public void setDetails(String details) {
        this.details = details;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}