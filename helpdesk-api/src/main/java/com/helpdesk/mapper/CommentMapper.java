
package com.helpdesk.mapper;

import org.springframework.stereotype.Component;

import com.helpdesk.document.CommentDocument;
import com.helpdesk.dto.request.CommentRequest;
import com.helpdesk.dto.response.CommentResponse;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public CommentDocument toDocument(
            CommentRequest request,
            Long ticketId,
            Long authorId) {

        CommentDocument comment = new CommentDocument();

        comment.setTicketId(ticketId);
        comment.setAuthorId(authorId);
        comment.setMessage(request.getMessage());
        comment.setCreatedAt(LocalDateTime.now());

        return comment;
    }

    public CommentResponse toResponse(CommentDocument comment) {

        CommentResponse response = new CommentResponse();

        response.setId(comment.getId());
        response.setTicketId(comment.getTicketId());
        response.setAuthorId(comment.getAuthorId());
        response.setMessage(comment.getMessage());
        response.setCreatedAt(comment.getCreatedAt());

        return response;
    }
}
