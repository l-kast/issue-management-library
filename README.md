# Issue Management Library (IML)

IML provides an effective way to standardize and communicate issues across services in microservice architectures. Although the library can be applied to any distributed system or service-oriented architecture, it is tailored for microservice architectures.

## Getting Started
This requires the library to be used for all architecture systems for effective usage. Furthermore, the configuration files (`issues.yaml`, `httpCodeMappings.properties`, `javaExceptionMappings.properties`) should be shared and identical.

Adapt the configurations `issues.yaml`, `httpCodeMappings.properties`, `javaExceptionMappings.properties` according to the needs of you system. Then deploy the library architecture wide.

## Usage
### Issues
Issues describe problems that occur in the system. An issue has a set of attributes to identify it for issue tracking and resolution.

**ID**: Unique identifier to track issues across microservices.

**Type**: Describes the issue type. It includes a name, description, and issue category. Developers can set the available issue types in `issues.yaml`, depending on the specific system properties.

**Severity**: This describes the issue's impact on the system and determines the required actions. There are three types.
- _Issues_ indicate a problem in a service. This can lead to malfunctioning but will not crash the services. No immediate action is required.
- _Errors_ indicate a service has a specific problem. This error has to be handled. Otherwise, the service will crash. 
- _Failures_ are service crashes. Other services cannot access the service anymore, and it must be restarted.

**Service**: A description of the services in which the issue occurred. Includes a service name and a service ID.

**Timestamp**: This can be set to improve debugging and facilitate the reconstruction of error scenarios.

**CauseIssue**: Can append another issue. If applied, it can facilitate root cause analysis of issues addressing one of the key challenges in microservices.

**Details**: Can be used to add details. For example one might append the underlying Java exception that caused the problem.

**CorrelationID**: If the system uses correlation IDs to trace requests across systems, this can be used to link an issue to a specific request chain using the requests correlation ID.

### Creation of Issues
The IssueManage is responsible for creating issues. It allows the creation of issues from scratch, and mapping issues from HTTP status codes and java exceptions.
Furthermore, issues can be mapped to JSON objects to allow for issue communication between systems. 

## Future Work
This system requires more extension for an effective usage. If multiple languages are parte of the technology stack the corresponding libraries must be developed. Furthermore, integration with tracing and log aggregation systems can improve the functionality of the system.