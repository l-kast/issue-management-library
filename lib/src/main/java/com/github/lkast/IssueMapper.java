package com.github.lkast;

import com.github.lkast.exceptions.IssueMappingException;
import com.github.lkast.model.IssueCategory;
import com.github.lkast.model.IssueName;
import com.github.lkast.model.IssueType;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * The IssueMapper class is responsible for mapping issues between different representations.
 * It provides methods to retrieve issue names, HTTP statuses, exceptions, descriptions, categories, and issue types.
 */
public class IssueMapper {
    private final Properties httpCodeMappings;
    private final Properties javaExceptionMappings;

    /**
     * The IssueMapper class is responsible for mapping HTTP status codes, exceptions,
     * and issue names to their corresponding values. It uses properties files to
     * store the mappings and provides methods to retrieve the mappings based on
     * specific inputs.
     * <p>
     * This class relies on the loadProperties method to load the mappings from
     * properties files. It throws an IOException if there is an error loading
     * the properties files.
     */
    public IssueMapper() {
        this.httpCodeMappings = new Properties();
        this.javaExceptionMappings = new Properties();
        try {
            loadProperties(httpCodeMappings, "httpCodeMappings.properties");
            loadProperties(javaExceptionMappings, "javaExceptionMappings.properties");
        } catch (IOException e) {
            // Here we wrap the IOException in a RuntimeException.
            // This will crash the program but ensures that errors won't go unnoticed.
            throw new RuntimeException("Error loading properties files", e);
        }
    }

    /**
     * Loads properties from a specified file into a Properties object.
     *
     * @param properties The Properties object to load the properties into.
     * @param filename   The name of the file to load the properties from.
     * @throws IOException If an I/O error occurs while loading the properties.
     */
    private void loadProperties(Properties properties, String filename) throws IOException {
        // Use try-with-resources to ensure the InputStream gets closed
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                throw new IOException("Couldn't load " + filename + " file");
            }
        }
    }

    /**
     * Retrieves the {@link IssueName} associated with the given HTTP status.
     *
     * @param httpStatus The HTTP status code.
     * @return The {@link IssueName} associated with the HTTP status.
     * @throws IllegalArgumentException if httpStatus is null.
     * @throws IssueMappingException if no mapping is found for the HTTP status.
     */
    public IssueName getIssueNameFromHttp(String httpStatus) {
        if (httpStatus == null) {
            throw new IllegalArgumentException("HTTP status cannot be null");
        }

        String issueString = this.httpCodeMappings.getProperty(httpStatus);

        if (issueString == null) {
            return IssueName.UNSPECIFIED_ISSUE;
        }

        try {
            return IssueName.valueOf(issueString);
        } catch (IllegalArgumentException e) {
            throw new IssueMappingException("Invalid issue name:" + issueString, e);
        }
    }

    /**
     * Retrieves the {@link IssueName} associated with the given exception.
     *
     */
    public IssueName getIssueNameFromException(String exception) {
        if (exception == null) {
            throw new IllegalArgumentException("Exception cannot be null");
        }

        String issueString = this.javaExceptionMappings.getProperty(exception);

        if (issueString == null) {
            return IssueName.UNSPECIFIED_ISSUE;
        }

        try {
            return IssueName.valueOf(issueString);
        } catch (IllegalArgumentException e) {
            throw new IssueMappingException("Invalid issue name:" + issueString, e);
        }
    }

    /**
     * Retrieves the HTTP status code associated with the given IssueName.
     *
     * @param issueName The IssueName for which to retrieve the HTTP status code.
     * @return The HTTP status code associated with the IssueName, or null if no match is found.
     */
    public String getHttpStatusFromIssueName(IssueName issueName) {
        return getFirstKeyMatchByValue(issueName, httpCodeMappings);
    }

    /**
     * Retrieves the exception name associated with the given IssueName.
     *
     * @param issueName The IssueName for which to retrieve the exception name.
     * @return The exception name associated with the IssueName, or null if no match is found.
     */
    public String getExceptionFromIssueName(IssueName issueName) {
        return getFirstKeyMatchByValue(issueName, javaExceptionMappings);
    }

    /**
     * Retrieves the IssueType based on the provided exception name.
     *
     * @param exception The name of the exception.
     * @return The IssueType object representing the issue.
     * @throws IllegalArgumentException if exception is null.
     * @throws IssueMappingException if no mapping is found for the exception name.
     */
    public IssueType getIssueTypeFromException(String exception) {
        IssueName issueName = getIssueNameFromException(exception);
        return new IssueType(issueName, getDescriptionForIssue(issueName), getCategoryForIssue(issueName));
    }

    /**
     * Retrieves the IssueType based on the provided HTTP status code.
     *
     * @param httpStatus The HTTP status code.
     * @return The IssueType object representing the issue.
     * @throws IllegalArgumentException if httpStatus is null.
     * @throws IssueMappingException if no mapping is found for the HTTP status code.
     */
    public IssueType getIssueTypeFromHttp(String httpStatus) {
        IssueName issueName = getIssueNameFromHttp(httpStatus);
        return new IssueType(issueName, getDescriptionForIssue(issueName), getCategoryForIssue(issueName));
    }

    /**
     * Retrieves the first key that matches the given value in the provided properties.
     *
     * @param issueName   the IssueName to match against the property values
     * @param properties  the Properties object to search for the matching key
     * @return the first key that matches the given value, or null if no match is found
     */
    private String getFirstKeyMatchByValue(IssueName issueName, Properties properties) {
        Enumeration<?> keys = properties.propertyNames();
        String issueNameString = issueName.name();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = properties.getProperty(key);

            if (value.equals(issueNameString)) {
                return key;
            }
        }

        return null;
    }

    /**
     * Retrieves the description for a specific issue.
     *
     * @param issueName The name of the issue.
     * @return The description of the issue, or null if no description is found.
     * @throws IllegalArgumentException if the issueName is null.
     */
    public String getDescriptionForIssue(IssueName issueName) {
        if (issueName == null) {
            throw new IllegalArgumentException("IssueName cannot be null");
        }

        Yaml yaml = new Yaml();
        InputStream inputStream = IssueMapper.class
                .getClassLoader()
                .getResourceAsStream("issues.yaml");

        Map<String, Map<String, Map<String, String>>> mappings = yaml.load(inputStream);

        for (Map.Entry<String, Map<String, Map<String, String>>> entry : mappings.entrySet()) {
            Map<String, Map<String, String>> issues = entry.getValue();
            for (Map.Entry<String, Map<String, String>> issue : issues.entrySet()) {
                if (issue.getKey().equalsIgnoreCase(issueName.name())) {
                    return issue.getValue().get("Description");
                }
            }
        }
        return null;
    }


    /**
     * Retrieves the category for a given issue.
     *
     * @param issueName The name of the issue.
     * @return The category of the issue.
     * @throws IllegalArgumentException if the issueName is null.
     * @throws IssueMappingException if no mapping is found for the issueName.
     */
    public IssueCategory getCategoryForIssue(IssueName issueName) {
        if (issueName == null) {
            throw new IllegalArgumentException("IssueName cannot be null");
        }

        Yaml yaml = new Yaml();
        InputStream inputStream;
        try {
            inputStream = IssueMapper.class
                    .getClassLoader()
                    .getResourceAsStream("issues.yaml");

            if (inputStream == null) {
                throw new FileNotFoundException("issues.yaml does not exist");
            }

            Map<String, Map<String, Map<String, String>>> mappings = yaml.load(inputStream);
            for (Map.Entry<String, Map<String, Map<String, String>>> entry : mappings.entrySet()) {
                String potentialCategory = entry.getKey();
                Map<String, Map<String, String>> issues = entry.getValue();
                for (Map.Entry<String, Map<String, String>> issue : issues.entrySet()) {
                    // If we found the issue under this category, return this category.
                    if (issue.getKey().toUpperCase().equals(issueName.name())) {
                        return IssueCategory.valueOf(potentialCategory.toUpperCase());
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new IssueMappingException("File not found", e);
        }
        throw new IssueMappingException("No Issue found for: " + issueName);
    }
}