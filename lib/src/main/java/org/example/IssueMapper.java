package org.example;

import org.example.exceptions.IssueMappingException;
import org.example.model.IssueCategory;
import org.example.model.IssueName;
import org.example.model.IssueType;
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
            e.printStackTrace();
        }
    }

    private void loadProperties(Properties properties, String filename) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
        if (inputStream != null) {
            properties.load(inputStream);
        } else {
            throw new IOException("Couldn't load " + filename + " file");
        }
    }

    public IssueName getIssueNameFromHttp(String httpStatus) {
        String issueString = this.httpCodeMappings.getProperty(httpStatus);
        return IssueName.valueOf(issueString);
    }

    public IssueName getIssueNameFromException(String exception) {
        String issueString = this.javaExceptionMappings.getProperty(exception);
        return IssueName.valueOf(issueString);
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
        return new IssueType(issueName, getExceptionFromIssueName(issueName), getCategoryForIssue(issueName));
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
        Yaml yaml = new Yaml();
        InputStream inputStream = IssueMapper.class
                .getClassLoader()
                .getResourceAsStream("errorMappings.yaml");

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
        Yaml yaml = new Yaml();
        InputStream inputStream;
        try {
            inputStream = IssueMapper.class
                    .getClassLoader()
                    .getResourceAsStream("errorMappings.yaml");

            if (inputStream == null) {
                throw new FileNotFoundException("errorMappings.yaml does not exist");
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