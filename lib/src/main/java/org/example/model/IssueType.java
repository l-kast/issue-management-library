package org.example.model;

public record IssueType(IssueName name, String description, IssueCategory category) {

    @Override
    public String toString() {
        return "IssueType{" +
                "name=" + name +
                ", description='" + description + '\'' +
                ", category=" + category +
                '}';
    }
}
