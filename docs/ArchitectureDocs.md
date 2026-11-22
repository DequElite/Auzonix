This document describes the architecture of Auzonix, its main components, their responsibilities, and the engineering decisions behind the current implementation.
# 1. Architecture Overview
## 1.1. Architecture Type
Auzonix follows a modular, component-based architecture within a Spring Boot application.

The system is divided into several components, each responsible for a specific part of the testing process. Components interact with each other to coordinate the work of Artificial Users and manage the overall testing lifecycle. This kind of architecture makes it possible to change different modules, without rewriting the entire system.

## 1.2. Architecture Principles
The current architecture is based on the following principles:
- **Separation of Responsibilities** — each component has its own area of responsibility.
- **Modularity** — system functionality is divided into independent logical components.
- **Loose Coupling** — components should avoid unnecessary dependencies on each other.
- **Simplicity** — avoid unnecessary abstractions and complexity.
- **Extensibility** — make it possible to introduce new functionality and improve existing components without unnecessarily changing the entire system.

## 1.3. Why This Architecture?
Auzonix consists of several different processes, such as website analysis, scenario generation, browser management, and Artificial User execution.

Separating these responsibilities into components makes the system easier to understand, maintain, and extend.
# 2. System Components
This section provides a brief overview of the main components of Auzonix.

## 2.1. App Analyzer (AI  & Browser Discovery)
**What is it?**
The component responsible for analyzing the provided web application and its intended functionality.

**Responsibilities:**
- Analyze the application description and purpose.
- Identify the application's intended functionality and tasks.
- Analyzing the page under test to identify interactive elements

**Implementation Idea:**
Uses AI-powered analysis to process application descriptions and extract information needed for testing.
To analyze a page, the system uses Playwright, which helps reduce AI token consumption and improves the understanding of the page.

**Why is it needed?**
Artificial Users need an understanding of the application before they can explore and interact with it.
## 2.2. Scenario Manager
**What is it?**
The component responsible for generating and managing testing scenarios.

**Responsibilities:**
- Generate scenarios based on application analysis.
- Consider Artificial User behavioral profiles.
- Provide scenarios for test execution.

**Implementation Idea:**
Uses AI-powered generation to create exploration scenarios based on the available application context and Artificial User characteristics (profile behaviours).

**Why is it needed?**
Scenarios provide an initial direction for Artificial Users without forcing them to follow a completely predefined sequence of actions.
## 2.3. AU Manager
**What is it?**
The component responsible for managing Artificial Users and their execution.

**Responsibilities:**
- Create and manage Artificial Users.
- Coordinate individual AU test executions.
- Manage their execution lifecycle.
- Support concurrent test execution.

**Implementation Idea:**
Coordinates Artificial User instances and their asynchronous execution.

**Why is it needed?**
It allows Auzonix to manage multiple Artificial Users with different behavioral profiles and run their tests independently.
## 2.4. ZooEngine
**What is it?**
The central orchestration component of Auzonix.

**Responsibilities:**
- Coordinate the overall testing process.
- Connect the main system components.
- Manage the execution flow between different testing stages.

**Implementation Idea:**
Acts as an orchestration layer that coordinates the work of other components.

**Why is it needed?**
It provides a central place for managing the testing process instead of distributing orchestration logic across unrelated components.
## 2.5. Error Collector
**What is it?**
The component responsible for collecting and organizing errors encountered during system execution.

**Responsibilities:**
- Collect system-level errors.
- Collect errors associated with individual Artificial Users.
- Provide information about errors during and after test execution.

**Implementation Idea:**
Uses error collection mechanisms to distinguish between general system errors and errors associated with individual AU executions.

**Why is it needed?**
It helps identify problems in both the system itself and individual Artificial User testing sessions.

##  2.6. Event Dispatcher
**What is it?**
The component responsible for dispatching events between different parts of system

**Responsibilities:**
- Dispatch events generated during system execution.
- Notify registered event listeners (handlers) about relevant events.
- Decouple event producers from event consumers.

**Implementation Idea:**
Uses an event-driven approach where producers publish events async without waiting for  consumers to finish their work.

**Why is it needed?**
It allows different components to communicate through events without requiring direct dependencies on each other. Moreover, it lowers the compontets dependencies among themselves.

## 2.7. Test Action Collector
**What is it?**
The component responsible for collecting and managing actions performed by Artificial Users during test execution.

**Responsibilities:**
- Collect actions performed by Artificial Users.
- Track the sequence of actions during test execution.
- Store or provide collected action data for further processing.

**Implementation Idea:**
Collects action events generated during Artificial User execution and organizes them into a structured representation using concurrent hash maps and synchronyzed lists to to guarantee consistency and concurrency control.

**Why is it needed?**
It provides information about how Artificial Users interact with a website, allowing the system to track their behavior and analyze test execution results.