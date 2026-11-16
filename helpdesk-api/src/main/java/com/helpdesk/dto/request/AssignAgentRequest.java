package com.helpdesk.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AssignAgentRequest {

    @NotNull(message = "Agent ID is required")
    @Positive(message = "Agent ID must be positive")
    private Long agentId;

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
}