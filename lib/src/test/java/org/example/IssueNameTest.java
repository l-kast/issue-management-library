package org.example;

import org.example.model.IssueName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class IssueNameTest {

    private Map<String, Object> internalIssues;
    private Map<String, Object> dependencyIssues;
    private Map<String, Object> unspecified;

    @BeforeEach
    public void setup() {
        loadYamlFile();
    }

    private void loadYamlFile() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("issues.yaml");
        Map<String, Object> obj = yaml.load(inputStream);
        internalIssues = (Map<String, Object>) obj.get("INTERNAL_ISSUE");
        dependencyIssues = (Map<String, Object>) obj.get("DEPENDENCY_ISSUE");
        unspecified = (Map<String, Object>) obj.get("UNSPECIFIED");
    }

    @Test
    public void testEnumsInYaml() {
        for (IssueName issue : IssueName.values()) {
            assertTrue(isIssuePresentInYaml(issue), "Enum " + issue.name() + " not found in YAML");
        }
    }

    private boolean isIssuePresentInYaml(IssueName issue) {
        return internalIssues.containsKey(issue.name())
                || dependencyIssues.containsKey(issue.name())
                || unspecified.containsKey(issue.name());
    }

    @Test
    public void testYamlInEnums() {
        assertKeysInEnum(internalIssues);
        assertKeysInEnum(dependencyIssues);
        assertKeysInEnum(unspecified);
    }

    private void assertKeysInEnum(Map<String, Object> issueMap) {
        issueMap.keySet().forEach(key -> {
            try {
                IssueName.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException ex) {
                fail("YAML key " + key + " not found in enums");
            }
        });
    }
}