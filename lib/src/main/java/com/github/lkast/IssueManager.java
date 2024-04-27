package com.github.lkast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.lkast.exceptions.IssueMappingException;
import com.github.lkast.model.Issue;
import com.github.lkast.model.IssueType;
import com.github.lkast.model.Service;
import com.github.lkast.model.Severity;

import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

public class IssueManager {
    private final IssueMapper issueMapper = new IssueMapper();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final Service service;

    public IssueManager(String serviceId, String serviceName) {
        this.service = new Service(serviceId, serviceName);
    }

    public Issue createIssue(IssueType issueType, Severity severity) {
        return new Issue(issueType, severity, service, Instant.now());
    }

    public Issue createIssue(IssueType issueType, Severity severity, Instant timeStamp) {
        return new Issue(issueType, severity, service, timeStamp);
    }

    public Issue createIssue(IssueType issueType, Severity severity, Instant timeStamp, Issue causeIssue, String note, UUID correlationID) {
        return new Issue(issueType, severity, service, timeStamp, causeIssue, note, correlationID);
    }

    public Issue createIssueFromJson(String json) {
        try {
            return objectMapper.readValue(json, Issue.class);
        } catch (JsonProcessingException e) {
            throw new IssueMappingException("Error occurred while processing JSON", e);
        }
    }

    public Issue createIssueFromException(Exception exception) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception cannot be null");
        }
        String exceptionClassName = exception.getClass().getName();
        IssueType issueType = issueMapper.getIssueTypeFromException(exceptionClassName);
        if (issueType == null) {
            throw new IssueMappingException("No IssueType found for exception: " + exceptionClassName);
        }
        Severity severity = Severity.ERROR; // Exceptions are treated as errors
        Instant timeStamp = Instant.now();

        StringBuilder detailsBuilder = new StringBuilder();
        detailsBuilder.append("Caused by ").append(exception.getClass().getName()).append("\n");
        detailsBuilder.append("Exception message: ").append(exception.getMessage()).append("\n");

        Throwable cause = exception.getCause();
        if (cause != null) {
            detailsBuilder.append("Cause: ").append(cause).append("\n");
        }

        Throwable[] suppressed = exception.getSuppressed();
        if (suppressed.length > 0) {
            detailsBuilder.append("Suppressed exceptions: ").append(Arrays.toString(suppressed)).append("\n");
        }

        detailsBuilder.append("Stack trace: ").append(Arrays.toString(exception.getStackTrace()));

        String details = detailsBuilder.toString();

        return new Issue(issueType, severity, service, timeStamp, details);
    }

    public Issue createIssueFromHttpStatus(String httpStatus) {
        if (httpStatus == null || httpStatus.isEmpty()) {
            throw new IllegalArgumentException("HTTP Status cannot be null or empty.");
        }

        String httpStatusCode = httpStatus.split(" ")[0]; // Extract the status code from httpStatus string
        IssueType issueType = issueMapper.getIssueTypeFromHttp(httpStatusCode);
        Severity severity = Severity.ERROR; // Http errors are treated as issues
        Instant timeStamp = Instant.now();
        String details = "Caused by HTTP Error: " + httpStatus;
        return new Issue(issueType, severity, service, timeStamp, details);
    }
}