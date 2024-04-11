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

public class IssueMapper {
    private final Properties httpCodeMappings;
    private final Properties javaExceptionMappings;

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

    public String getHttpStatusFromIssueName(IssueName issueName) {
        return getFirstKeyMatchByValue(issueName, httpCodeMappings);
    }

    public String getExceptionFromIssueName(IssueName issueName) {
        return getFirstKeyMatchByValue(issueName, javaExceptionMappings);
    }

    public IssueType getIssueTypeFromException(String exception) {
        IssueName issueName = getIssueNameFromException(exception);
        return new IssueType(issueName, getDescriptionForIssue(issueName), getCategoryForIssue(issueName));
    }

    public IssueType getIssueTypeFromHttp(String httpStatus) {
        IssueName issueName = getIssueNameFromHttp(httpStatus);
        return new IssueType(issueName, getDescriptionForIssue(issueName), getCategoryForIssue(issueName));
    }

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