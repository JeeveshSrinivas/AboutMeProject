package com.helpdesk.controller;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.helpdesk.dto.request.AssignAgentRequest;
import com.helpdesk.dto.request.CreateTicketRequest;
import com.helpdesk.dto.response.TicketResponse;
import com.helpdesk.service.TicketService;

import jakarta.validation.Valid;

import com.helpdesk.dto.response.PageResponse;
import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api/v2/tickets")
@SecurityRequirement(name = "bearerAuth")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PreAuthorize("hasRole('REQUESTER')")
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody CreateTicketRequest request) {

        TicketResponse response = ticketService.createTicket(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('REQUESTER','AGENT', 'ADMIN')")
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long ticketId) {

        TicketResponse response = ticketService.getTicketById(ticketId);

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('REQUESTER', 'AGENT', 'ADMIN')")
    @GetMapping
    public ResponseEntity<PageResponse<TicketResponse>> getAllTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<TicketResponse> response = ticketService.getAllTickets(status, priority, page, size);

        return ResponseEntity.ok(response);
    }

   @PreAuthorize("hasAnyRole('REQUESTER', 'AGENT')")
    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(
            @PathVariable Long ticketId,
            @RequestParam TicketStatus status) {

        TicketResponse response = ticketService.updateTicketStatus(ticketId, status);

        return ResponseEntity.ok(response);
    }

    // @PreAuthorize("hasRole('AGENT')")
    // @PatchMapping("/{ticketId}/claim")
    // public ResponseEntity<TicketResponse> claimTicket(
    //         @PathVariable Long ticketId) {

    //     TicketResponse response = ticketService.claimTicket(ticketId);

    //     return ResponseEntity.ok(response);
    // }

    // @PreAuthorize("hasRole('ADMIN')")
    // @PatchMapping("/{ticketId}/assign")
    // public ResponseEntity<TicketResponse> assignAgent(
    //         @PathVariable Long ticketId,
    //         @Valid @RequestBody AssignAgentRequest request) {

    //     TicketResponse response = ticketService.assignAgent(ticketId, request);

    //     return ResponseEntity.ok(response);
    // }


    @PreAuthorize("hasAnyRole('ADMIN','AGENT')")
    @PatchMapping("/{ticketId}/assign")
    public ResponseEntity<TicketResponse> assignAgent(
            @PathVariable Long ticketId,
            @Valid @RequestBody(required = false) AssignAgentRequest request) {

            Long agentId = (request == null) ? null : request.getAgentId();

        TicketResponse response = ticketService.assignTicket(ticketId, agentId);

        return ResponseEntity.ok(response);
    }
}