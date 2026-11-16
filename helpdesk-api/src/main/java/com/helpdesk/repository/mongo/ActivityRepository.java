package com.helpdesk.repository.mongo;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.helpdesk.document.ActivityDocument;

public interface ActivityRepository
        extends MongoRepository<ActivityDocument, String> {

    List<ActivityDocument> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    boolean existsByTicketIdAndAction(Long ticketId, String action);
}