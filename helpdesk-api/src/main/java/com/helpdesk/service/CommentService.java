
package com.helpdesk.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.helpdesk.entity.User;
import com.helpdesk.repository.jpa.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;

import com.helpdesk.document.CommentDocument;
import com.helpdesk.dto.request.CommentRequest;
import com.helpdesk.dto.response.CommentResponse;
import com.helpdesk.event.TicketActivityEvent;
import com.helpdesk.mapper.CommentMapper;
import com.helpdesk.repository.jpa.TicketRepository;
import com.helpdesk.repository.jpa.UserRepository;
import com.helpdesk.repository.mongo.CommentRepository;

@Service
public class CommentService {

        private final CommentRepository commentRepository;
        private final TicketRepository ticketRepository;
        private final CommentMapper commentMapper;
        private final ApplicationEventPublisher eventPublisher;
        private final UserRepository userRepository;

        public CommentService(
                        CommentRepository commentRepository,
                        TicketRepository ticketRepository,
                        CommentMapper commentMapper,
                        ApplicationEventPublisher eventPublisher,
                        UserRepository userRepository) {

                this.commentRepository = commentRepository;
                this.ticketRepository = ticketRepository;
                this.commentMapper = commentMapper;
                this.eventPublisher = eventPublisher;
                this.userRepository = userRepository;
        }

        @Transactional
        public CommentResponse addComment(
                        Long ticketId,
                        CommentRequest request) {

                // Verify that the ticket exists in SQL Server
                ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Ticket not found with ID: " + ticketId));

                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                String email = authentication.getName();

                User author = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));
                // Convert request into MongoDB document
                CommentDocument comment = commentMapper.toDocument(request, ticketId, author.getId());

                // Save comment in MongoDB
                CommentDocument savedComment = commentRepository.save(comment);

                // Publish activity event
                eventPublisher.publishEvent(
                                new TicketActivityEvent(
                                                ticketId,
                                                author.getId(),
                                                "COMMENT_ADDED",
                                                "A new comment was added to the ticket"));

                return commentMapper.toResponse(savedComment);
        }

        @Transactional(readOnly = true)
        public List<CommentResponse> getCommentsByTicketId(Long ticketId) {

                // Verify that the ticket exists
                ticketRepository.findById(ticketId)
                                .orElseThrow(() -> new RuntimeException(
                                                "Ticket not found with ID: " + ticketId));

                // Retrieve comments in chronological order
                return commentRepository
                                .findByTicketIdOrderByCreatedAtAsc(ticketId)
                                .stream()
                                .map(commentMapper::toResponse)
                                .toList();
        }
}
