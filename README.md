# CSCI-5448-OOD-Project-Mocks-And-Stubs

Testing in Object-Oriented (OO) systems introduces distinct challenges due to features such as inheritance, encapsulation, and polymorphism. While these concepts
enhance modularity and reusability, they often complicate component isolation during testing.
This project investigates how mocking and stubbing can
simplify testing in OO contexts. The goal is to improve testability, maintainability, and isolation through
the strategic use of test doubles. We demonstrate these ideas using a Java-based Library Management System (LMS) developed in two versions:

* Bad Design ```(com.example.library.bad)``` - Demonstrates anti-patterns with tight coupling and static state

* Good Design ```(com.example.library.good)``` - Applies strong OO principles with Dependency Injection and Interfaces

### Dependencies

    Names: Mukund Mahesan, Thomas Davis
    Java Version: Oracle OpenJDK 24.0.2

##### Key Libraries
* JUnit 5
* Mockito (5.5.0)
* Gradle - Build automation tool


### Build and Test Instructions

To build, run ```./gradlew build```

To run tests, clone the repository in IntelliJ and run them in IntelliJ

### Key Test Demonstrations
1. Stubbing for State Verification (```LibraryServiceStubTest```)

    Uses manual stub (```StubDataStore```) to provide fixed responses
    Demonstrates isolation without external dependencies
    Best for testing logic paths based on different data states

2. Mocking for Behavior Verification (```LibraryServiceMockTest```)

    Uses Mockito to verify interactions with dependencies
    Ensures correct method calls and parameters
    Critical for testing side effects without executing them

3. Spies for Partial Mocking (```LibraryServiceSpyTest```)

    Wraps real objects while enabling verification
    Combines real behavior with interaction tracking
    Useful for testing legacy code or when both real state and verification are needed

4. Argument Captors (```LibraryServiceArgumentCaptorTest```)

    Captures arguments passed to mocked methods
    Allows detailed assertions on complex objects
    Verifies object state at exact moment of method call

5. External Dependency Mocking (```LibraryServiceNotificationTest```)

    Mocks ```NotificationService``` to avoid sending real emails
    Tests remain fast, isolated, and reliable
    Demonstrates testing external system integrations

6. Bad Design Challenges (```LibraryAppTest```)

    Documents difficulties testing procedural code
    Shows impact of static state, tight coupling, and I/O dependencies
    Requires complex setup with stream redirection and manual cleanup

### Running the Application

While this is primarily a testing demonstration project, you can explore the implementations:
* The ```LibraryApp class``` in ```com.example.library.bad``` demonstrates anti-patterns.
* The LibraryService class in com.example.library.good demonstrates proper OO design with:
  * Dependency Injection
  * Interface-based abstractions
  * Separation of concerns
  * Testable architecture

### Key Findings
Our analysis reveals:
1. **Design Dictates Testability**: "Bad" implementation achieved <50% code coverage, while "Good" implementation achieved >90%
2. **Mocking Requires Good Design**: Frameworks like Mockito are ineffective without proper dependency injection and interfaces
3. **Complementary Tools**: Stubs and Mocks serve different purposes:
    * Stubs: Simulate state for input verification
    * Mocks: Verify behavior for output verification
