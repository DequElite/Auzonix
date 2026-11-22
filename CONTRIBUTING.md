First of all, thank you for your interest in Auzonix! Any help, feedback, and ideas are welcome.
Auzonix is still in its early development stage, so the contribution process is currently limited.
## 1. How to Contribute
At the moment, contributions are handled through GitHub Issues. Direct Pull Requests are not accepted yet.

You can help Auzonix in the following ways:
### Bug Reports
Found something that does not work as expected? You can report it by creating an Issue using the **Bug Report** template.

Please provide as much information as possible to help us understand and reproduce the problem.

See [Bug Report](./docs/BugReport.md) for more details.
### Feature Requests
Have an idea that could improve Auzonix or introduce new possibilities?

You can create an Issue using the **Feature Request** template and describe your suggestion.

See [Feature Request](./docs/FeatureRequest.md) for more details.
### General Feedback
Have thoughts, questions, or feedback about Auzonix?

Feel free to open a GitHub Issue and share your experience, ideas, or suggestions. You don't need to have a specific bug report or feature request — general feedback is also valuable!
## 2. How to Run Auzonix Locally
You can also run Auzonix on your local machine to explore its functionality, experiment with the system, and help identify potential issues.

**The full template will be here later. If you do not understand how to use it, please, give us a feedback on Issues**
### Prerequisites
Before running Auzonix, make sure you have the following installed:
- Java 21
- Maven
- An API key for a supported AI provider
### Setup
1. Clone the repository
2. Configure the application:
   ```yml
    spring:  
    autoconfigure:  
    exclude:  
    - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration  
      - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration  
      - org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration  
      - org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration  
      application:  
      name: auzonix
    
    langchain4j:  
    open-ai:  
    chat-model:  
    api-key: <your-openai-api-key>
    model-name: <your-ai-model> (recomended is gpt-6-luna: cheaper and faster)
    timeout: 10m  
    log-requests: true  
    log-responses: true
    
    logging:  
    level:  
    dequelite.auzonix: DEBUG
    ```
1. Build and run the application:
    ```bash
    mvn spring-boot:run
    ```

### Usage
Open [localhost:8080](http://localhost:8080/swagger-ui/index.html) to see the Swagger documentation of API. Auzonix using Rest API contracts for interaction.

**Note:** Additional configuration may be required depending on your local environment.