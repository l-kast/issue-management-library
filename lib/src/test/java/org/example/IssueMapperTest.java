package org.example;

import org.example.model.IssueCategory;
import org.example.model.IssueName;
import org.example.model.IssueType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IssueMapperTest {
    private static IssueMapper mapper;

    @BeforeAll
    public static void setUp() {
        mapper = new IssueMapper();
    }

    @Test
    public void getCategoryForIssueWithValidInput() {
        IssueName issueName = IssueName.VALIDATION_ISSUE;
        IssueCategory result = mapper.getCategoryForIssue(issueName);
        Assertions.assertEquals(IssueCategory.INTERNAL_ISSUE, result);
    }

    @Test
    public void getDescriptionForIssueWithValidInput() {
        String description = mapper.getDescriptionForIssue(IssueName.VALIDATION_ISSUE);
        assertEquals("Issues related to input validation or constraints not being met within the microservice.", description);
    }

    @Test
    public void getIssueNameFromHttpWithValidInput() {
        String httpStatus = "400";
        IssueName issueName = mapper.getIssueNameFromHttp(httpStatus);
        assertEquals(IssueName.VALIDATION_ISSUE, issueName);
    }

    @Test
    public void getIssueNameFromExceptionWithValidInput() {
        String exceptionName = "java.lang.IllegalArgumentException";
        IssueName issueName = mapper.getIssueNameFromException(exceptionName);
        assertEquals(IssueName.VALIDATION_ISSUE, issueName);
    }

    @Test
    public void getHttpStatusFromIssueNameWithValidInput() {
        IssueName issueName = IssueName.VALIDATION_ISSUE;
        String httpStatus = mapper.getHttpStatusFromIssueName(issueName);
        assertEquals("400", httpStatus);
    }

    @Test
    public void getExceptionFromIssueNameWithValidInput() {
        IssueName issueName = IssueName.VALIDATION_ISSUE;
        String exception = mapper.getExceptionFromIssueName(issueName);
        assertEquals("java.lang.IllegalArgumentException", exception);
    }

    @Test
    public void getIssueTypeFromExceptionWithValidInput() {
        String exceptionName = "java.lang.IllegalArgumentException";
        IssueType issueType = mapper.getIssueTypeFromException(exceptionName);
        assertEquals(new IssueType(IssueName.VALIDATION_ISSUE,
                        "Issues related to input validation or constraints not being met within the microservice.",
                        IssueCategory.INTERNAL_ISSUE)
                , issueType);
    }

    @Test
    public void getIssueTypeFromHttpWithValidInput() {
        String httpStatus = "400";
        IssueType issueType = mapper.getIssueTypeFromHttp(httpStatus);
        assertEquals(new IssueType(IssueName.VALIDATION_ISSUE,
                        "Issues related to input validation or constraints not being met within the microservice.",
                        IssueCategory.INTERNAL_ISSUE)
                , issueType);
    }
}