package org.example;

import java.time.Instant;
import java.util.UUID;

public class ErrorObject {
    private ErrorType errorType;

    private Instant timeStamp;

    private Service service;

    private UUID correlationID;
}
