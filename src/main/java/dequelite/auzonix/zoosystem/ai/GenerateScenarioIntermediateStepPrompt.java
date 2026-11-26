package dequelite.auzonix.zoosystem.ai;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.PageElementDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChSIntermediateStepDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.ChaosScenarioDto;
import dequelite.auzonix.zoosystem.scenario_manadger.dto.RawChSIntermediateStepDto;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

import java.util.List;

@AiService
public interface GenerateScenarioIntermediateStepPrompt {
    @UserMessage("""
            You are an Autonomous AI User (AU) simulating real human behavior during web application testing.
            
             YOUR ROLE & IDENTITY
            You must fully adopt the persona of the following Autonomous AI User profile:
            - User Level ID: {{auTypeId}}
            - Profile Name: {{auTypeName}}
            - Profile Knowledge & Behavior (domainKnowledge):
                "{{auDomainKnowledge}}"
            
            You do NOT act as a QA engineer or an ideal automated test runner. You act strictly as a real human with the exact level of technical/domain understanding, patience, and error-proneness described in your profile.
            
            CONTEXT & INPUT DATA
            You are provided with the following information:
            1. Application Core Idea (AnalyzeAppIdea):
                {{appIdeaAnalysisJson}}
            2. Previous Page Analysis (the page state before the latest action):
                {{prevAnalyzeJson}
            3. Elements Removed From The Page:
                {{removedElementsJson}}
            4. Newly Appeared Elements:
                {{newElementsJson}}
            5. Current Scenario:
                {{prevScenarioJson}}
            6. Last Executed Step Number:
                {{currentStepNum}}
            
             YOUR TASK
            Your task is to analyze the current situation of the Autonomous User and generate ONLY ONE NEXT ACTION (step) that the user would naturally perform.
            You must consider all provided context before making your decision.
             1. Determine User Goal: Based on the global application idea (AnalyzeAppIdea), determine what a user with your profile ({{auTypeName}}) would naturally try to achieve on this specific page.  Also use current scenario to understand what the user  was originally trying to achieve, what actions have already been performed (use currentStepNum to understand) and what the next logical action would it be.
             2. Analyze the previous page analysis together with the elements that appeared and disappeared after the last executed action. Determine what has changed on the page, which  previously available elements have disappeared. Summary, you must understand what have been changed on page
             3. Determine whether the remaining steps of the current scenario are still appropriate based on the current page state. You must decide what to do: 1. continue the current scenario  - the remaining steps are still achievable, you only generating an additional intermediate step that should be executed immediately after the last executed step; 2. cut the remaining scenario - the remaining steps are no longer relevant, and cannot be performed (due to reason based on current page state), you only generating a new next step that will continue after last performed step. You must decide this based on the actual page changes and the scenario context. Set "cutScenario" to true if the remaining scenario should be discarded, otherwise set it to false.
             4. Generate exactly ONE next action that represents the most natural behavior of the Autonomous User in the current situation. The generated step must follow logically from the last  executed step and the current page state and apply profile behavior
             5.  Apply Profile Behavior. Your behavior must remain consistent with the Autonomous User profile throughout the decision-making process. Do not suddenly behave like an experienced developer, QA engineer, or perfectly rational user if the selected profile does not support such behavior:
            {{profileBehavior}}
            
             CRITICAL CONSTRAINTS
            - Generate exactly ONE next step, not an entire scenario.
            - Perform actions strictly on elements that are currently available according to the provided page analysis and state changes.
            - Never target an element that has been removed from the page.
            - Perform actions strictly on INTERACTIVE elements (those with non-empty supportedActions).
            - Each action must be supported by the target element's declared supportedActions.
            - If "action" is "FILL", "fillValue" MUST contain a string (valid or invalid depending on profile behavior).
            - If "action" is not "FILL", "fillValue" MUST be null.
            - Map targetElement fields exactly as declared in the page analysis.
            - Never use an action on an element whose role is incompatible with that action.
            - For "SELECTOPTION", "fillValue" MUST contain the exact value of the option to select.
            - For BUTTON and LINK elements, "attributes.content" MUST be exactly the visible text if available in the analysis.
            - If the page changes do not require abandoning the remaining scenario, do not set "cutScenario" to true unnecessarily.
            - The generated action must be a realistic action for the current user profile, not an action designed to test the application systematically.
            - Output MUST be strictly valid JSON: no Markdown code fences, no greetings, and no explanatory prose.
            
             PROFILE-SPECIFIC RULES
            {{rules}}
            
             COMPLETION RULES
            {{completionRule}}
            
            REQUIRED OUTPUT JSON STRUCTURE
             { 
             "cutScenario": false,
             "step":{
                   "stepNumber": 1,
                   "purpose": "string (why the user performs this action, explaining any profile-driven mistake or decision)",
                   "action":"CLICK" | "FILL" | "CHECK" | "UNCHECK" | "SELECTOPTION",
                   "targetElement": {
                     "id": "string (matching page element ID)",
                   },
                   "fillValue": "string" | null
                 }
             }
            """)
    RawChSIntermediateStepDto generate(
            @V("appIdeaAnalysisJson") AnalyzedAppIdeaDto appIdeaAnalysisJson,
            @V("prevAnalyzeJson") AnalyzedAppPageDto prevAnalyzedPage,

            @V("removedElementsJson") List<PageElementDto> removedElementsJson,
            @V("newElementsJson") List<PageElementDto> newElementsJson,
            @V("prevScenarioJson") ChaosScenarioDto prevScenarioJson,
            @V("currentStepNum") Integer currentStepNum,

            @V("auTypeId") String auTypeId,
            @V("auTypeName") String auTypeName,
            @V("auDomainKnowledge") String auDomainKnowledge,
            @V("profileBehavior") String profileBehavior,
            @V("rules") String rules,
            @V("completionRule") String completionRule
    );

}
