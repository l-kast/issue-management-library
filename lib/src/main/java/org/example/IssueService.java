/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Issue;
import org.example.model.IssueType;
import org.example.model.Service;

import java.time.Instant;

public class IssueService {
    private final IssueMapper issueMapper = new IssueMapper();

    private final Service service;

    public IssueService(Service service) {
        this.service = service;
    }

    public IssueService(int serviceId, String serviceName) {
        this.service = new Service(serviceId, serviceName);
    }

    public Issue fromJson(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(json, Issue.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public String toJson(Issue issue) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            // Serialize the Person object to JSON
            return objectMapper.writeValueAsString(issue);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public Issue fromException(Exception exception) {
        IssueType issueType = new IssueType(issueMapper.getIssueTypeFromException(exception.getClass().getSimpleName()));
        Issue issue = new Issue(issueType);
        issue.setTimeStamp(Instant.now());
        issue.setService(service);
        return issue;
    }
}