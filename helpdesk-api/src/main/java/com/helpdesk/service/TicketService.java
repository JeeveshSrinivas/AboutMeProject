package com.helpdesk.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.context.ApplicationEventPublisher;

import com.helpdesk.event.TicketActivityEvent;
import com.helpdesk.event.TicketCreatedEvent;
import com.helpdesk.dto.request.AssignAgentRequest;
import com.helpdesk.dto.request.CreateTicketRequest;
import com.helpdesk.dto.response.TicketResponse;
import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.TicketPriority;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.enums.UserRole;
import com.helpdesk.exception.InvalidTicketTransitionException;
import com.helpdesk.mapper.TicketMapper;
import com.helpdesk.repository.jpa.TicketRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.helpdesk.dto.response.PageResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.helpdesk.entity.User;
import com.helpdesk.repository.jpa.UserRepository;
import com.helpdesk.enums.UserRole;

@Service
public class TicketService {

        private final TicketRepository ticketRepository;
        private final TicketMapper ticketMapper;
        private final ApplicationEventPublisher eventPublisher;
        private final UserRepository userRepository;

        public TicketService(
                        TicketRepository ticketRepository,
                        TicketMapper ticketMapper,
                        ApplicationEventPublisher eventPublisher,
                        UserRepository userRepository) {
                this.ticketRepository = ticketRepository;
                this.ticketMapper = ticketMapper;
                this.eventPublisher = eventPublisher;
                this.userRepository = userRepository;
        }

        @Transactional
        public TicketResponse createTicket(CreateTicketRequest request) {

                Ticket ticket = ticketMapper.toEntity(request);

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String email = authentication.getName();

                User requester = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

                // Only requesters can create tickets
                if (requester.getRole() != UserRole.REQUESTER) {
                        throw new IllegalArgumentException(
                                        "Only requesters can create tickets");
                }

                ticket.setRequester(requester);

                ticket.setStatus(TicketStatus.OPEN);

                LocalDateTime now = LocalDateTime.now();

                ticket.setDueAt(calculateDueDate(ticket.getPriority(), now));

                Ticket savedTicket = ticketRepository.save(ticket);

                eventPublisher.publishEvent(
                                new TicketCreatedEvent(
                                                savedTicket.getId(),
                                                savedTicket.getTitle()));

                return ticketMapper.toResponse(savedTicket);
        }

        private LocalDateTime calculateDueDate(
                        TicketPriority priority,
                        LocalDateTime createdAt) {

                return switch (priority) {
                        case HIGH -> createdAt.plusHours(4);
                        case MEDIUM -> createdAt.plusHours(24);
                        case LOW -> createdAt.plusHours(72);
                };
        }

        @Transactional(readOnly = true)
        public TicketResponse getTicketById(Long ticketId) {

                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Ticket not found with ID: " + ticketId));

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String email = authentication.getName();

                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Requesters can view only their own tickets
                if (currentUser.getRole() == com.helpdesk.enums.UserRole.REQUESTER) {

                        if (ticket.getRequester() == null ||
                                        !ticket.getRequester().getId().equals(currentUser.getId())) {

                                throw new AccessDeniedException(
                                                "You are not allowed to view this ticket");
                        }
                }

                return ticketMapper.toResponse(ticket);
        }

        @Transactional
        public TicketResponse claimTicket(Long ticketId) {

                // Get the logged-in agent
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String email = authentication.getName();

                User agent = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("Agent not found"));

                if (agent.getRole() != com.helpdesk.enums.UserRole.AGENT) {
                        throw new IllegalArgumentException(
                                        "Only agents can claim tickets");
                }

                // Find the ticket
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

                // Check whether the ticket is already assigned
                if (ticket.getAssignedAgent() != null) {
                        throw new IllegalArgumentException(
                                        "Ticket is already assigned to an agent");
                }

                // Only OPEN tickets can be claimed
                if (ticket.getStatus() != TicketStatus.OPEN) {
                        throw new IllegalArgumentException(
                                        "Only OPEN tickets can be claimed");
                }

                // Assign the ticket to the logged-in agent
                ticket.setAssignedAgent(agent);

                Ticket updatedTicket = ticketRepository.save(ticket);

                // Record the assignment activity
                eventPublisher.publishEvent(
                                new TicketActivityEvent(
                                                updatedTicket.getId(),
                                                agent.getId(),
                                                "TICKET_CLAIMED",
                                                "Ticket claimed by agent: " + agent.getEmail()));

                return ticketMapper.toResponse(updatedTicket);
        }

        @Transactional
        public TicketResponse assignAgent(Long ticketId, AssignAgentRequest request) {

                // Get the logged-in admin
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String adminEmail = authentication.getName();

                User admin = userRepository.findByEmail(adminEmail)
                                .orElseThrow(() -> new RuntimeException("Admin not found"));

                if (admin.getRole() != com.helpdesk.enums.UserRole.ADMIN) {
                        throw new IllegalArgumentException(
                                        "Only admins can assign tickets");
                }

                // Find the ticket
                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

                // Prevent assigning agents to completed tickets
                if (ticket.getStatus() == TicketStatus.RESOLVED ||
                                ticket.getStatus() == TicketStatus.CLOSED) {
                        throw new IllegalArgumentException(
                                        "Cannot assign an agent to a resolved or closed ticket");
                }

                // Find the selected agent
                User agent = userRepository.findById(request.getAgentId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Agent not found with ID: " + request.getAgentId()));

                // Ensure the selected user is actually an agent
                if (agent.getRole() != com.helpdesk.enums.UserRole.AGENT) {
                        throw new IllegalArgumentException("Selected user is not an agent");
                }

                // Assign the agent
                ticket.setAssignedAgent(agent);

                Ticket updatedTicket = ticketRepository.save(ticket);

                // Record the assignment activity
                eventPublisher.publishEvent(
                                new TicketActivityEvent(
                                                updatedTicket.getId(),
                                                admin.getId(),
                                                "AGENT_ASSIGNED",
                                                "Ticket assigned to agent: " + agent.getEmail()));

                return ticketMapper.toResponse(updatedTicket);
        }

        @Transactional(readOnly = true)
        public PageResponse<TicketResponse> getAllTickets(
                        TicketStatus status,
                        TicketPriority priority,
                        int page,
                        int size) {

                if (page < 0 || size < 1 || size > 100) {
                        throw new IllegalArgumentException(
                                        "Page must be non-negative and size must be between 1 and 100");
                }

                Pageable pageable = PageRequest.of(page, size);

                Specification<Ticket> specification = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String email = authentication.getName();

                User currentUser = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                if (currentUser.getRole() == com.helpdesk.enums.UserRole.REQUESTER) {

                        specification = specification.and(
                                        (root, query, cb) -> cb.equal(root.get("requester").get("id"),
                                                        currentUser.getId()));
                }

                if (status != null) {
                        specification = specification.and(
                                        (root, query, cb) -> cb.equal(root.get("status"), status));
                }

                if (priority != null) {
                        specification = specification.and(
                                        (root, query, cb) -> cb.equal(root.get("priority"), priority));
                }

                Page<Ticket> ticketPage = ticketRepository.findAll(
                                specification,
                                pageable);

                return new PageResponse<>(
                                ticketPage.getContent()
                                                .stream()
                                                .map(ticketMapper::toResponse)
                                                .toList(),
                                ticketPage.getNumber(),
                                ticketPage.getSize(),
                                ticketPage.getTotalElements(),
                                ticketPage.getTotalPages(),
                                ticketPage.isLast());
        }

        @Transactional
        public TicketResponse updateTicketStatus(
                        Long ticketId,
                        TicketStatus newStatus) {

                Ticket ticket = ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Ticket not found with ID: " + ticketId));

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String email = authentication.getName();

                User actor = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                TicketStatus currentStatus = ticket.getStatus();

                // Check whether the requested transition is valid
                boolean validTransition = switch (currentStatus) {
                        case OPEN -> newStatus == TicketStatus.IN_PROGRESS;
                        case IN_PROGRESS -> newStatus == TicketStatus.RESOLVED;
                        case RESOLVED -> newStatus == TicketStatus.CLOSED;
                        case CLOSED -> false;
                };

                if (!validTransition) {
                        throw new InvalidTicketTransitionException(
                                        "Cannot change ticket status from "
                                                        + currentStatus + " to " + newStatus);
                }

                // Check whether the logged-in user is authorized
                boolean authorized = switch (currentStatus) {

                        case OPEN, IN_PROGRESS ->
                                actor.getRole() == UserRole.AGENT
                                                && ticket.getAssignedAgent() != null
                                                && ticket.getAssignedAgent().getId().equals(actor.getId());

                        case RESOLVED ->
                                actor.getRole() == UserRole.REQUESTER
                                                && ticket.getRequester() != null
                                                && ticket.getRequester().getId().equals(actor.getId());

                        case CLOSED -> false;
                };

                if (!authorized) {
                        throw new AccessDeniedException(
                                        "You are not authorized to perform this ticket status transition");
                }

                ticket.setStatus(newStatus);

                Ticket updatedTicket = ticketRepository.save(ticket);

                eventPublisher.publishEvent(
                                new TicketActivityEvent(
                                                updatedTicket.getId(),
                                                actor.getId(),
                                                "STATUS_CHANGED",
                                                "Ticket status changed from " + currentStatus
                                                                + " to " + newStatus));

                return ticketMapper.toResponse(updatedTicket);
        }

}