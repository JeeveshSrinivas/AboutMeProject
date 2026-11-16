package com.helpdesk.mapper;

import org.springframework.stereotype.Component;

import com.helpdesk.dto.request.CreateTicketRequest;
import com.helpdesk.dto.response.TicketResponse;
import com.helpdesk.entity.Ticket;

@Component
public class TicketMapper {

    public Ticket toEntity(CreateTicketRequest request) {

        Ticket ticket = new Ticket();

        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(request.getPriority());

        return ticket;
    }

    public TicketResponse toResponse(Ticket ticket) {

        TicketResponse response = new TicketResponse();

        response.setId(ticket.getId());
        response.setTitle(ticket.getTitle());
        response.setDescription(ticket.getDescription());
        response.setPriority(ticket.getPriority());
        response.setStatus(ticket.getStatus());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());
        response.setDueAt(ticket.getDueAt());

        response.setRequesterId(
                ticket.getRequester() != null
                        ? ticket.getRequester().getId()
                        : null);

        response.setAssignedAgentId(
                ticket.getAssignedAgent() != null
                        ? ticket.getAssignedAgent().getId()
                        : null);

        return response;
    }
}