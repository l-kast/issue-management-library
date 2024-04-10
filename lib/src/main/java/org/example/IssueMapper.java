package org.example;

import java.io.IOException;
import java.io.InputStream;
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

    public String getIssueTypeFromHttp(String httpStatus) {
        return this.httpCodeMappings.getProperty(httpStatus);
    }

    public String getIssueTypeFromException(String exception) {
        return this.javaExceptionMappings.getProperty(exception);
    }
}