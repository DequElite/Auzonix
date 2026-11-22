## Project Status
Auzonix is an actively developed experimental project that has only recently started its development journey.

Currently, the system is at the MVP stage. Its main purpose is to test the core idea of AI Monkey-Chaos Engineering (AI MCE) and explore the possibilities of using AI to simulate human-like user behavior during website testing.

At this stage, Auzonix is primarily a proof of concept rather than a production-ready testing solution. Its current functionality is limited, and the system is still evolving.

Bugs, unexpected behavior, incomplete features, and limitations are expected. The current implementation should be considered experimental and used primarily for exploring and evaluating the concept.
## Current Capabilities
The current MVP provides the following capabilities:
- **Website Analysis** — analyzes the provided website description, purpose, and tasks to understand its intended functionality.
- **Page Exploration** — uses Playwright to inspect website pages and identify interactive elements.
- **Scenario Generation** — it  generates  exploration scenarios with help of AI based on website analysis and different users types (profilese, personalities).
- **Adaptive Interaction** — allows system to change the scenario and create new steps when page structure has been changed by using AI (like Scenario Generation) .
- **Concurrent Testing** — supports running multiple tests concurrently with different behavioral profiles.
- **Error Collection** — collects system errors and errors encountered during test execution.
- **Test Lifecycle Tracking** — provides information about the execution lifecycle of individual Artificial Users (it also tracks deciding - steps).
## Known Limitations
Auzonix is still in its early development stage. The following limitations apply to the current MVP:
- **Limited Testing Scope** — the current implementation does not cover every possible website structure or interaction pattern.
- **Experimental AI Behavior** — Artificial Users do not perfectly reproduce real human behavior. Their decisions depend on AI models, available context, and generated scenarios.
- **Limited Interaction Support** — only a defined set of browser elements and interactions is currently supported.
- **No Visual Understanding** — the current implementation does not use visual page analysis to understand website interfaces.
- **Unpredictable Results** — the same website may produce different exploration paths and results between test runs.
- **Incomplete Error Detection** — Auzonix cannot guarantee that all logical bugs, UI issues, or other problems will be detected.
- **No Guaranteed Stability** — crashes, failed actions, and unexpected execution behavior may occur.
- **System can use only Open AI (will be upgraded later)**
- **System can not discover the whole webapp, only one page (this update is on the planning stage)**
# ***But!***
Auzonix is under active development, and we are constantly working to improve its features and capabilities.

You can also become part of this journey and help us improve the project! See the [Contributing](../CONTRIBUTING.md) file for more details.