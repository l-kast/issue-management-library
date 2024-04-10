package org.example;

import org.example.model.IssueName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * IssueNameTest is a JUnit test class for validating the correctness of the IssueName enum
 * and its usage in YAML files.
 */
public class IssueNameTest {

    /**
     * Tests the presence of all enum values in a YAML file.
     * It loads the YAML file and checks if each enum value exists in the specified sections of the file.
     */
    @Test
    public void testEnumsInYaml() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("errorMappings.yaml");
        Map<String, Object> obj = yaml.load(inputStream);
        Map<String, Object> internalIssues = (Map<String, Object>) obj.get("INTERNAL_ISSUES");
        Map<String, Object> dependencyIssues = (Map<String, Object>) obj.get("DEPENDENCY_ISSUES");

        for (IssueName issue : IssueName.values()) {
            boolean existsInYaml = internalIssues.containsKey(issue.name())
                    || dependencyIssues.containsKey(issue.name());
            assertTrue(existsInYaml, "Enum " + issue.name() + " not found in YAML");
        }
    }

    /**
     * The testYamlInEnums method is a JUnit test method that validates the correctness of loading values from a YAML file
     * and checking if they exist in the IssueName enum.
     */
    @Test
    public void testYamlInEnums() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("errorMappings.yaml");
        Map<String, Object> obj = yaml.load(inputStream);
        Map<String, Object> internalIssues = (Map<String, Object>) obj.get("INTERNAL_ISSUES");
        Map<String, Object> dependencyIssues = (Map<String, Object>) obj.get("DEPENDENCY_ISSUES");

        internalIssues.keySet().forEach(key -> {
            try {
                IssueName.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException ex) {
                fail("YAML key " + key + " not found in enums");
            }
        });

        dependencyIssues.keySet().forEach(key -> {
            try {
                IssueName.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException ex) {
                fail("YAML key " + key + " not found in enums");
            }
        });
    }
}