package org.example.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.exceptions.IssueMappingException;

import java.time.Instant;
import java.util.UUID;

public class Issue {

    private final UUID issueID;
    private IssueType issueType;
    private Severity severity;
    private Service service;
    private Instant timeStamp;
    private Issue causeIssue;
    private String details;
    private UUID correlationID; // Set request or message id here

    private Issue() {
        this.issueID = UUID.randomUUID();
    }

    public Issue(IssueType issueType, Severity severity, Service service, Instant timeStamp) {
        this();
        if (issueType == null || severity == null || service == null || timeStamp == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.issueType = issueType;
        this.severity = severity;
        this.service = service;
        this.timeStamp = timeStamp;
    }

    public Issue(IssueType issueType, Severity severity, Service service, Instant timeStamp, String details) {
        this();
        if (issueType == null || severity == null || service == null || timeStamp == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.issueType = issueType;
        this.severity = severity;
        this.service = service;
        this.timeStamp = timeStamp;
        this.details = details;
    }

    public Issue(IssueType issueType, Severity severity, Service service, Instant timeStamp, Issue causeIssue, String details, UUID correlationID) {
        this();
        if (issueType == null || severity == null || service == null || timeStamp == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }
        this.issueType = issueType;
        this.severity = severity;
        this.service = service;
        this.timeStamp = timeStamp;
        this.causeIssue = causeIssue;
        this.details = details;
        this.correlationID = correlationID;
    }

    public UUID getIssueID() {
        return issueID;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public Severity getSeverity() {
        return severity;
    }

    public Service getService() {
        return service;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    public Issue getCauseIssue() {
        return causeIssue;
    }

    public String getDetails() {
        return details;
    }

    public UUID getCorrelationID() {
        return correlationID;
    }

    public String toJsonString() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonString;
        try {
            jsonString = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IssueMappingException("Unable to generate JSON string", e);
        }
        return jsonString;
    }

    @Override
    public String toString() {
        return "Issue{" +
                "issueID=" + issueID +
                ", issueType=" + issueType +
                ", severity=" + severity +
                ", service=" + service +
                ", timeStamp=" + timeStamp +
                ", causeIssue=" + causeIssue +
                ", details='" + details + '\'' +
                ", correlationID=" + correlationID +
                '}';
    }
}