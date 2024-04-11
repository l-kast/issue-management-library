package com.github.lkast;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcesTest {

    @Test
    void testResources() {
        checkResource("issues.yaml");
        checkResource("httpCodeMappings.properties");
        checkResource("javaExceptionMappings.properties");
    }

    private void checkResource(String resourceName) {
        // Assume that the resources are in the src/main/resources/ folder
        Path resourcePath = Paths.get("src/main/resources", resourceName);

        File resourceFile = resourcePath.toFile();

        assertTrue(resourceFile.exists(), resourceName + " does not exist at the expected location.");
        assertTrue(resourceFile.isFile(), resourceName + " is not a regular file.");
        assertTrue(Files.isReadable(resourcePath), resourceName + " is not readable.");
    }
}
