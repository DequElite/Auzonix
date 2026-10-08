package dequelite.auzonix.zoosystem.ai;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTasksDto;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService
public interface AnalyzeAppTasksPrompt {

    @UserMessage("""
            **Task:**
            You are a Senior QA Engineer analyzing a web application before testing it.
            
            Your task is to identify the main tasks and functional capabilities that the application provides to its users.
            
            You are given:
            1. The original application description.
            2. A previously generated analysis of the application's general idea.
            
            Use both sources to understand what the application is intended to do and what users should be able to accomplish with it.
            
            Analyze and determine:
            1. **Application tasks** — the main practical tasks users can accomplish using the application.
            2. **Task purpose** — what each task allows the user to achieve.
            3. **Required actions** — the general actions a user needs to perform to accomplish each task.
            
            Focus on **user-facing functionality and intended behavior**.
            
            Do not analyze:
            - source code;
            - database structure;
            - internal services;
            - implementation details;
            - specific UI elements unless they are explicitly described;
            - bugs or potential failures.
            
            Do not invent functionality. Every identified task must be supported by the provided application description or the previous application idea analysis.
            
            **Input**
            Application Description
            {{appDocument}}
            
            Application Idea Analysis
            {{appIdeaAnalysis}}
            
            **Output**
            Return the analysis as valid JSON.
            The response must contain a list of the application's main tasks.
            
            Each task must contain:
            - `taskName` — a concise name of the task.
            - `taskDescription` — what the user wants to accomplish.
            - `taskPurpose` — why the task exists and what result it provides to the user.
            - `requiredActions` — a list of general actions required to accomplish the task.
            
            **JSON Structure**
            {
            "tasks": [
            	{
            		"taskName": "string",
            		"taskDescription": "string",
            		"taskPurpose": "string",
            		"requiredActions": [
            			"string"
            		]
            	}
            ]
            }
            """)
    List<AnalyzedAppTasksDto> analyze(@V("appDocument") String appDocument, @V("appIdeaAnalysis") AnalyzedAppIdeaDto appIdeaAnalysis);
}
