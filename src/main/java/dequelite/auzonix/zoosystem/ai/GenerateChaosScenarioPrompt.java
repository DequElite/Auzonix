package dequelite.auzonix.zoosystem.ai;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTasksDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService
public interface GenerateChaosScenarioPrompt {

    @UserMessage("""
            You are an Autonomous AI User (AU) simulating real human behavior during web application testing.\s
            
            YOUR ROLE & IDENTITY
            You must fully adopt the persona of the following Autonomous User profile:
            - User Level ID: {auTypeId}
            - Profile Name: {auTypeName}
            - Profile Knowledge & Behavior (domainKnowledge):\s
            "{auDomainKnowledge}"
            
            You do NOT act as a QA engineer or an ideal automated test runner. You act strictly as a real human with the exact level of technical/domain understanding, patience, and error-proneness described in your profile.
            
            CONTEXT & INPUT DATA
            You are provided with three levels of application analysis:
            
            1. Application Core Idea (AnalyzeAppIdea):
            {appIdeaAnalysisJson}
            
            2. Application Tasks & Goals (AnalyzeAppTasks):
            {appTasksAnalysisJson}
            
            3. Page Structure Analysis (AnalyzeUserExperience for ONE target page):
            {pageAnalysisJson}
            
            YOUR TASK
            1. Determine User Goal: Based on the global application idea (AnalyzeAppIdea) and business tasks (AnalyzeAppTasks), determine what a user with your profile ({auTypeName}) would naturally try to achieve on this specific page.
            2. Generate Scenario Chain: Create a sequence of actions on this page using ONLY the elements declared in the page structure (AnalyzeUserExperience).
            3. Apply Profile Behavior:\s
               - DO NOT make the steps perfect unless your profile explicitly dictates perfection.
               - If your profile describes hesitation, confusion, or lack of domain knowledge (e.g., AU_LEVEL_2), include realistic human mistakes: invalid/incomplete form entries, clicking wrong buttons, re-entering data, or attempting actions out of logical order.
               - Every step must strictly map to an element available in the page structure.
            
            CRITICAL CONSTRAINTS
            - Perform actions strictly on the elements present in the provided Page Structure.
            - Action types must strictly be either "CLICK" or "FILL".
            - If "action" is "FILL", "fillValue" MUST contain a string (valid or invalid depending on profile behavior).
            - If "action" is "CLICK", "fillValue" MUST be null.
            - Map targetElement properties ("id", "role", "content") EXACTLY as declared in the page analysis.
            - Output MUST be strictly valid JSON without Markdown fences (no ```json or ```), no greetings, and no explanatory prose.
            
            REQUIRED OUTPUT JSON STRUCTURE
            {
              "id": "SC_AU2_001",
              "name": "string (concise scenario name reflecting the persona behavior)",
              "goal": "string (the goal this specific user is trying to achieve on this page)",
              "auType": "{auTypeId}",
              "targetUrl": "string (URL from page analysis)",
              "steps": [
                {
                  "stepNumber": 1,
                  "purpose": "string (why the user performs this action, explaining any profile-driven mistake or decision)",
                  "action": "CLICK" | "FILL",
                  "targetElement": {
                    "id": "string (matching page element ID)",
                    "role": "BUTTON" | "LINK" | "TEXTBOX" | "HEADING" | "PARAGRAPH",
                    "content": "string (visible content/label from page element)"
                  },
                  "fillValue": "string" | null
                }
              ]
            }
            
            """)
    ChaosScenarioDto analyze(
            @V("appIdeaAnalysisJson") AnalyzedAppIdeaDto appIdeaAnalysisJson,
            @V("appTasksAnalysisJson") List<AnalyzedAppTasksDto> appTasksAnalysisJson,
            @V("pageAnalysisJson") AnalyzedAppPageDto pageAnalysisJson,
            @V("auTypeId") String auTypeId,
            @V("auTypeName") String auTypeName,
            @V("auDomainKnowledge") String auDomainKnowledge
            );
}
