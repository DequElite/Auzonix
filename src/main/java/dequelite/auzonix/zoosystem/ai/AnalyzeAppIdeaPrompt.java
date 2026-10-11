package dequelite.auzonix.zoosystem.ai;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface AnalyzeAppIdeaPrompt {

    @UserMessage("""
            Task:
            You are a Senior QA Engineer analyzing a web application before testing it.
            
            Your task is to understand the application's high-level concept, purpose, and user goals. Do not analyze implementation details, source code, database structure, or specific bugs at this stage.
            
            Analyze the provided application idea and determine:
            1. **Application type** — identify what kind of web application it is based on its purpose and functionality.
            2. **Core idea** — explain what the application is fundamentally about.
            3. **Main purpose** — explain what the application provides to its users.
            4. **Application goal** — determine what the application is intended to achieve.
            5. **User goals** — identify the main things users want to accomplish using the application.
            
            Use only information that can be reasonably inferred from the provided application description. Do not invent features, functionality, or user goals that are not supported by the input.
            
            Input:
            Application idea document:
            {{appDocument}}
            
            Output:
            Return the analysis as valid JSON.
            
            The response must contain the following fields:
            - `applicationType` — a concise classification of the application.
            - `coreIdea` — a clear description of the application's fundamental concept.
            - `mainPurpose` — what the application provides to its users.
            - `applicationGoal` — what the application is intended to achieve.
            - `userGoals` — a list of the main goals users can pursue in the application.
            
            Each user goal must contain:
            - `goalTask` — a short name describing the task.
            - `goalText` — a clear description of what the user wants to accomplish.
            
            JSON Structure:
            {
            "applicationType": "string",
            "coreIdea": "string",
            "mainPurpose": "string",
            "applicationGoal": "string",
            "userGoals": [
            	{
            		"goalTask": "string",
            		"goalText": "string"
            	}
            ]
            
            """)
    AnalyzedAppIdeaDto analyze(@V("appDocument") String appDocument);
}
