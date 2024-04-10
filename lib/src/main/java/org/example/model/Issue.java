package org.example.model;

import java.time.Instant;
import java.util.UUID;

public class Issue {
    private final UUID issueID;
    private IssueType issueType;
    private Severity severity;
    private Service service;
    private Instant timeStamp;
    private Issue causeIssue;
    private String note;
    private UUID correlationID; // Set request or message id here

    private Issue() {
        this.issueID = UUID.randomUUID();
    }

    public Issue(IssueType issueType, Severity severity, Service service, Instant timeStamp) {
        this();
        this.issueType = issueType;
        this.severity = severity;
        this.service = service;
        this.timeStamp = timeStamp;
    }

    public Issue(IssueType issueType, Severity severity, Service service, Instant timeStamp, Issue causeIssue, String note, UUID correlationID) {
        this();
        this.issueType = issueType;
        this.severity = severity;
        this.service = service;
        this.timeStamp = timeStamp;
        this.causeIssue = causeIssue;
        this.note = note;
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

    public String getNote() {
        return note;
    }

    public UUID getCorrelationID() {
        return correlationID;
    }
}