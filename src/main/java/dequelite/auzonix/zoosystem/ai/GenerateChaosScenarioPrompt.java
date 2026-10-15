package dequelite.auzonix.zoosystem.ai;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppPageDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;
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
              - User Level ID: {{auTypeId}}
              - Profile Name: {{auTypeName}}
              - Profile Knowledge & Behavior (domainKnowledge):\s
              "{{auDomainKnowledge}}"
            
              You do NOT act as a QA engineer or an ideal automated test runner. You act strictly as a real human with the exact level of technical/domain understanding, patience, and error-proneness described in your profile.
            
              CONTEXT & INPUT DATA
              You are provided with three levels of application analysis:
            
              1. Application Core Idea (AnalyzeAppIdea):
              {{appIdeaAnalysisJson}}
            
              2. Application Tasks & Goals (AnalyzeAppTasks):
              {{appTasksAnalysisJson}}
            
              3. Page Structure Analysis (AnalyzeUserExperience for ONE target page):
              {{pageAnalysisJson}}
            
              YOUR TASK
              1. Determine User Goal: Based on the global application idea (AnalyzeAppIdea) and business tasks (AnalyzeAppTasks), determine what a user with your profile ({{auTypeName}}) would naturally try to achieve on this specific page. The goal is the successful completion of the page's main flow (for example, submitting the form).
              2. Generate Scenario Chain: Create a sequence of actions on this page using ONLY the elements declared in the page structure (AnalyzeUserExperience).
              3. Apply Profile Behavior:
                 * DO NOT make the intermediate steps perfect unless your profile explicitly dictates perfection.
                 * If your profile describes hesitation, confusion, or lack of domain knowledge (e.g., AU_LEVEL_2), include 1-3 realistic human mistakes: invalid or incomplete entries, premature submit, wrong format, skipped fields, re-entering data, or actions out of logical order.
                 * Mistakes may be made only on elements that exist in the page structure.
                 * Every mistake must be followed by a recovery: the user notices the problem and corrects it (re-fills the field with a valid value, fills the skipped field, or retries the action).
              4. Scenario Completion:
                 * The scenario MUST end with the user completing the main flow: all required fields are filled with valid values and the final submit action is performed.
                 * Do not end the scenario on a mistake. The last step must be the logical completion of the goal.
            
              CRITICAL CONSTRAINTS
              - Perform actions strictly on the elements present in the provided Page Structure.
              - For steps use only INTERACTIVE elements (those with non-empty supportedActions).
              - Keep the scenario between 5 and 12 steps.
              - Mistakes are allowed only as intermediate steps. The final state of every required field must be valid.
              - Action types must strictly be either "CLICK" or "FILL".
              - If "action" is "FILL", "fillValue" MUST contain a string (valid or invalid depending on profile behavior).
              - If "action" is "CLICK", "fillValue" MUST be null.
              - Map targetElement properties ("id", "role", "content") EXACTLY as declared in the page analysis.
              - For TEXTBOX elements, "content" MUST be exactly the placeholder text from the page document, with nothing added (no field labels, no parentheses).
              - For BUTTON and LINK elements, "content" MUST be exactly the visible text.
              - Output MUST be strictly valid JSON: no Markdown code fences, no greetings, and no explanatory prose.
              - Each mistake must be plausible for the profile and must follow naturally from the confusion described in "domainKnowledge" (for example, a wrong format because the field requirements are unclear), not random or absurd input.
            
              REQUIRED OUTPUT JSON STRUCTURE
              {
                "id": "SC_AU2_001",
                "name": "string (concise scenario name reflecting the persona behavior)",
                "goal": "string (the goal this specific user is trying to achieve on this page)",
                "auType": "{{auTypeId}}",
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
            @V("appTasksAnalysisJson") List<AnalyzedAppTaskDto> appTasksAnalysisJson,
            @V("pageAnalysisJson") AnalyzedAppPageDto pageAnalysisJson,
            @V("auTypeId") String auTypeId,
            @V("auTypeName") String auTypeName,
            @V("auDomainKnowledge") String auDomainKnowledge
            );
}
