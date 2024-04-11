package com.github.lkast;

import com.github.lkast.model.IssueCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class IssueCategoryTest {

    private Map<String, Object> obj;

    @BeforeEach
    public void setup() {
        loadYamlFile();
    }

    private void loadYamlFile() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("issues.yaml");
        obj = yaml.load(inputStream);
    }

    @Test
    public void testCategoryEnumsInYaml() {
        for (IssueCategory category : IssueCategory.values()) {
            assertTrue(isCategoryPresentInYaml(category), "Enum " + category.name() + " not found in YAML");
        }
    }

    private boolean isCategoryPresentInYaml(IssueCategory category) {
        return obj.containsKey(category.name());
    }

    @Test
    public void testYamlInCategoryEnums() {
        obj.keySet().forEach(key -> {
            try {
                IssueCategory.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException ex) {
                fail("YAML key " + key + " not found in enums");
            }
        });
    }
}
