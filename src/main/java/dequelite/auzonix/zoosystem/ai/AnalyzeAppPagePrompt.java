package dequelite.auzonix.zoosystem.ai;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTasksDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService
public interface AnalyzeAppPagePrompt {

    @UserMessage("""
            Task:
            
            You are a Senior QA Engineer analyzing a specific page of a web application before automated testing.
            Your task is to analyze a single page document and extract its structural layout, key interactive elements, and possible user actions.
            
            You are given:
            1. The specific page documentation.
            2. A previously generated analysis of the application's core idea.
            3. A previously generated analysis of the application's tasks.
            
            Analyze the provided inputs and determine:
            1. Page Metadata:
               - Page name.
               - Page purpose.
               - Page URL or route (mark as "unknown" if not provided).
            
            2. Interactive & Visible Elements (on this page):
               - Numerical ID (e.g., "1", "2", "3").
               - Role: strictly one of ["BUTTON", "LINK", "TEXTBOX", "HEADING", "PARAGRAPH"].
               - Content: visible text label, button text, or field placeholder/description.
               - Target link (href): destination URL if applicable (optional, omit or set to null if not present).
               - Supported actions: array containing allowed low-level actions strictly from ["CLICK", "FILL"].
            
            3. Possible User Actions (on this page):
              - Numerical ID (e.g., "1", "2").
              - Name: concise action name.
              - Description: what the action accomplishes and how it relates to the tasks defined in Application Tasks Analysis.
              - Required elements: list of element numerical IDs involved in performing this action.
            
            Important Rules:
            - Analyze ONLY the provided page document in the context of the general application idea and tasks.
            - Do not invent elements, pages, or functionality not supported by the document.
            - Use simple numerical IDs for elements (e.g., "1", "2") and actions (e.g., "1", "2").
            - Assign roles ONLY from the specified allowed list: "BUTTON", "LINK", "TEXTBOX", "HEADING", "PARAGRAPH".
            - Supported actions per element MUST strictly contain only "CLICK" and/or "FILL".
            - Maintain strict alignment between required elements in user actions and the declared element IDs.
            -  Every possible action on the page MUST directly map to or support one of the user goals or application tasks defined in the provided Application Idea Analysis and Application Tasks Analysis.
            
            Input:
            
            Page Document:
            {{pageDocument}}
            
            Application Idea Analysis:
            {{appIdeaAnalysis}}
            
            Application Tasks Analysis:
            {{appTasksAnalysis}}
            
            Output:
            
            Return the page analysis as valid JSON. Do not wrap the JSON in Markdown code blocks or prose.
            
            JSON Structure:
            
            {
              "name": "string",
              "purpose": "string",
              "url": "string",
              "elements": [
                {
                  "id": "1",
                  "role": "BUTTON",
                  "content": "string",
                  "href": "string",
                  "supportedActions": [
                    "CLICK"
                  ]
                }
              ],
              "possibleActions": [
                {
                  "id": "1",
                  "name": "string",
                  "description": "string",
                  "requiredElements": [
                    "1"
                  ]
                }
              ]
            }
            """)
    AnalyzedAppPageDto analyze(
            @V("pageDocument") String pageDocument,
            @V("appIdeaAnalysis") AnalyzedAppIdeaDto appIdeaAnalysis,
            @V("appTasksAnalysis") List<AnalyzedAppTasksDto> appTasksAnalysis);
}
