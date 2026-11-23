package com.helpdesk.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.helpdesk.document.RevokedToken;

public interface RevokedTokenRepository
        extends MongoRepository<RevokedToken, String> {
}