package org.example.model;

/**
 * Enumeration representing the severity levels of issues within a service.
 *
 * <ul>
 *   <li>{@link #ISSUE} - Indicates an issue that might affect the service's functionality to some extent. The service, however, appears to work as expected from an external perspective.</li>
 *   <li>{@link #ERROR} - Signifies a more serious problem that directly affects the service's functionality. The service can continue to operate, but only if the error is properly handled and resolved.</li>
 *   <li>{@link #FAILURE} - Represents a grave situation where the service is basically unavailable, possibly due to a crash or a similar event. Remediation is required for the service to operate again.</li>
 * </ul>
 */
public enum Severity {
    ISSUE, ERROR, FAILURE
}
