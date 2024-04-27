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

    /**
     * Constructs a new IssueManager object with the provided service ID and service name.
     *
     * @param serviceId   The ID of the service.
     * @param serviceName The name of the service.
     */
    public IssueManager(String serviceId, String serviceName) {
        this.service = new Service(serviceId, serviceName);
    }

    /**
     * Creates an Issue object with the given parameters.
     *
     * @param issueType The type of the issue.
     * @param severity  The severity level of the issue.
     * @return The created Issue object.
     */
    public Issue createIssue(IssueType issueType, Severity severity) {
        return new Issue(issueType, severity, service, Instant.now());
    }

    /**
     * Creates an Issue object with the given parameters.
     *
     * @param issueType  The type of the issue.
     * @param severity   The severity level of the issue.
     * @param timeStamp  The timestamp of the issue.
     * @return The created Issue object.
     */
    public Issue createIssue(IssueType issueType, Severity severity, Instant timeStamp) {
        return new Issue(issueType, severity, service, timeStamp);
    }

    /**
     * Creates an Issue object with the given parameters.
     *
     * @param issueType     The type of the issue.
     * @param severity      The severity level of the issue.
     * @param timeStamp     The timestamp of the issue.
     * @param causeIssue    The issue that caused this issue (can be null).
     * @param note          Additional note or details about the issue (can be null).
     * @param correlationID The correlation ID of the issue (can be null).
     * @return The created Issue object.
     */
    public Issue createIssue(IssueType issueType, Severity severity, Instant timeStamp, Issue causeIssue, String note, UUID correlationID) {
        return new Issue(issueType, severity, service, timeStamp, causeIssue, note, correlationID);
    }

    /**
     * Creates an Issue object by deserializing JSON.
     *
     * @param json The JSON representation of an Issue object.
     * @return The deserialized Issue object.
     * @throws IssueMappingException If an error occurs while processing the JSON.
     */
    public Issue createIssueFromJson(String json) {
        try {
            return objectMapper.readValue(json, Issue.class);
        } catch (JsonProcessingException e) {
            throw new IssueMappingException("Error occurred while processing JSON", e);
        }
    }

    /**
     * Creates an Issue object based on the provided Exception.
     *
     * @param exception The Exception object from which to create the Issue.
     * @return The created Issue object.
     * @throws IllegalArgumentException If exception is null.
     * @throws IssueMappingException If no IssueType mapping is found for the exception.
     */
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

    /**
     * Creates an Issue object based on the provided HTTP status code.
     *
     * @param httpStatus The HTTP status code.
     * @return The created Issue object.
     * @throws IllegalArgumentException if httpStatus is null or empty.
     */
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