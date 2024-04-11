package org.example.model;

/**
 * Enumeration representing various categories of failures or issues that can occur in the system.
 *
 * <ul>
 *   <li>{@link #INTERNAL_ISSUE} - Represents failures arising from within the system. These could range from code bugs, improper error and exception handling, database failure, scalability limitations, to failures in microservice-level logging or monitoring.</li>
 *   <li>{@link #DEPENDENCY_ISSUE} - Depicts issues stemming from dependencies, both internal and external. This includes outages or disruptions of downstream microservices, internal service outages, external (third-party) service outages, failures of internal libraries, failures of external (third-party) libraries, and a dependency not meeting its SLA.</li>
 *   <li>{@link #UNSPECIFIED} - Used for failures that cannot be clearly categorized into the two categories above, or where the cause is unknown.</li>
 * </ul>
 */
public enum IssueCategory {
    INTERNAL_ISSUE,
    DEPENDENCY_ISSUE,
    UNSPECIFIED
}
