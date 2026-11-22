Welcome to Auzonix repository! Here you can explore  new experimental system, and help itgrow.
## What is Auzonix?
**Auzonix** - its not just a new AI-agent, its an autonomous testing system that manages Artificial Users which have their own presonality, interact with your website chaotically and trying to imitate real-user behaivor by using AI-powered solutions. System allows Artificial Users to break your web app or round  use it as intended, without using any E2E tests or predefined scenarios that can be perfect and couldnt reveal real bugs.
## Why Auzonix?
**Auzonix** does not try to use your site as intended, instead it creates Artificial Users that try to behave like real users, simulating different levels of understanding: from someone who sees the site for the first time, to a person who does everything not the way it should be. You no longer have to write everything yourself - you can watch how real users could interact with your site in production, without making your website publicly available. Moreover, this approach makes it possible to spot logical bugs or inconsistencies on your site, before they reach actual users.
## How does it work?
**For now, the system is in the alpha stage (experimental) and has just started its journey, so it does not have many features yet, but we are actively developing it. See** [**System Limitations**](SystemLimitations)**.**

First of all, Auzonix explores your app idea, tasks, and purpose to understand how real users could interact with your web app in different ways.

In the next step, the system analyzes your pages using the Playwright library to understand the structure of your site and identify which elements it can interact with.

Then, it generates a chaotic scenario based on the previously collected data and AU (Artificial User) profile behavior, using AI to create a dynamic exploration path.

Finally, the system starts executing the AU tests by following the generated scenarios. However, the system does not strictly follow them. During the test, it can dynamically adapt or change the scenario whenever the page structure changes. Auzonix detects these structural changes, analyzes the updated page, and uses AI to decide what steps should be taken next and how the original scenario should be adapted to the new state of the application.

After all, the system returns a list of errors (system errors or errors encountered during AU testing) and the lifecycle of every AU.

Furthermore, Auzonix can run multiple tests concurrently (like several users interacting with a website simultaneously), with different behavioral profiles to generate diverse scenarios.
## Tech stack
**Auzonix is built on the Java ecosystem.**
### Backend
- Java 21
- Spring Boot
- Maven
### AI & LLM
- LangChain4j
### Browser Automation
- Playwright
## Contributing
First of all, thank you for your interest in Auzonix! Any help, feedback, and ideas are welcome in [Contributing](CONTRIBUTING.md)
## Architecture Docs
See the [Architecture Docs](./docs/ArchitectureDocs.md) file to understand system workflow superficially.
## System Limitations
Auzonix is an actively developed experimental project that has only recently started its development journey.
For more information about current restrictions and known issues, see  [**System Limitations**](./docs/SystemLimitations.md)
## Versioning
Auzonix uses a custom versioning system combined with Semantic Versioning (SemVer).
See the [VERSIONING](Versioning) file for details.
To see the current system version see the [VERSIONING](Versioning) file also.
## License
Auzonix is licensed under the Apache License 2.0.
See the [LICENSE](./LICENSE) file for details.
## Contacts
- **GitHub:** [@DequElite](https://github.com/DequElite)
- **LinkedIn:** https://www.linkedin.com/in/maxim-rozhnov-398055330/
- **Telegram:** https://t.me/@dequelite

If you have questions, suggestions, or ideas about Auzonix, feel free to contact me or open an issue in this repository.

