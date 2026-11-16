
package com.helpdesk.event;

public record TicketActivityEvent(
    Long ticketId,
    Long actorId,
    String action,
    String details
) {
}
