package com.helpdesk.repository.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.helpdesk.document.CommentDocument;

public interface CommentRepository
        extends MongoRepository<CommentDocument, String> {

    List<CommentDocument> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}