package com.helpdesk.event;

public record TicketActivityEvent(
        Long ticketId,
        Long employeeId,
        String employeeRole,
        String action,
        String details,
        String eventOrigin
) {
}