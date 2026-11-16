package com.helpdesk.event;

public record TicketCreatedEvent(
    Long ticketId,
    String title
) {
}