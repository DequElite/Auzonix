package dequelite.auzonix.zoosystem.ai;

import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppIdeaDto;
import dequelite.auzonix.zoosystem.app_analyzer.dto.AnalyzedAppTaskDto;
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
               - Numerical ID (e.g., "EL_1", "EL_2", "EL_3").
               - Role: strictly one of ["BUTTON", "LINK", "TEXTBOX", "HEADING", "PARAGRAPH", "CHECKBOX", "RADIO", "SWITCH", "COMBOBOX", "OPTION"].
               - Attributes:
                 - content: The actual visible text or content of the element.
                 - id: The element's HTML id attribute, used as a CSS selector.
                 - className: The element's HTML class attribute, used as a CSS selector.
                 - placeholder: The placeholder text of an input element. Similar to content, but specifically used for input fields.
               - Target link (href): destination URL if applicable (optional, omit or set to null if not present).
               - Supported actions: array containing allowed low-level actions strictly from ["CLICK", "FILL", "CHECK", "UNCHECK", "SELECTOPTION"].
            
            ROLE DEFINITIONS:
            
            - HEADING: A page heading or section title. Non-interactive element.
            - PARAGRAPH: A text paragraph or descriptive content. Non-interactive element.
            - BUTTON: A clickable button that triggers an interface action.
            - LINK: A clickable link that navigates to another page or location.
            - TEXTBOX: An input field where users enter text or other data.
            - CHECKBOX: A checkbox that can be checked or unchecked.
            - RADIO: A radio button used to select an option.
            - SWITCH: A toggle control used to enable or disable a setting.
            - COMBOBOX: A dropdown or selection list containing available options.
            - OPTION: An individual selectable option within a COMBOBOX or similar selection component.
            
            ACTION DEFINITIONS:
            
            - CLICK: Click an interactive element to activate it or trigger its associated action.
            - FILL: Enter text or data into a TEXTBOX.
            - CHECK: Check or enable a CHECKBOX, RADIO, or SWITCH.
            - UNCHECK: Uncheck or disable a CHECKBOX or SWITCH.
            - SELECTOPTION: Select an available OPTION through its associated COMBOBOX or selection component.
            
            3. Possible User Actions (on this page):
              - Numerical ID (e.g., "1", "2").
              - Name: concise action name.
              - Description: what the action accomplishes and how it relates to the tasks defined in Application Tasks Analysis.
              - Required elements: list of element numerical IDs involved in performing this action.
            
            Important Rules:
            - Analyze ONLY the provided page document in the context of the general application idea and tasks.
            - Do not invent elements, pages, attributes, or functionality not supported by the document.
            - Assign each element a unique numerical ID using the "EL_" prefix (e.g., "EL_1", "EL_2").
            - Assign each possible action a unique numerical ID using the "PA_" prefix (e.g., "PA_1", "PA_2").
            - Assign roles ONLY from the specified allowed list.
            - HEADING and PARAGRAPH are non-interactive elements and MUST have an empty supportedActions array.
            - Supported actions MUST reflect the actual functionality of the element, not assumed functionality based solely on its role.
            - Do not assign actions to elements that are not interactive.
            - Extract attributes exactly as documented or identifiable from the provided page document.
            - The "content" attribute represents the actual content of the element, not its HTML id, class, or placeholder.
            - The "id" and "class" attributes represent HTML attributes that can be used as CSS selectors. Do not confuse them with the numerical element ID.
            - The "placeholder" attribute MUST only be populated for input elements when a placeholder is present.
            - If an attribute cannot be identified, use an empty string rather than inventing a value.
            - The "href" attribute MUST contain the actual destination URL when available. Otherwise, use null.
            - Maintain strict alignment between requiredElements in possibleActions and the declared element IDs.
            - Every possible action on the page MUST directly map to or support one of the user goals or application tasks defined in the provided Application Idea Analysis and Application Tasks Analysis.
            
            Input:
            
            Page Document:
            {pageDocument}
            
            Application Idea Analysis:
            {appIdeaAnalysis}
            
            Application Tasks Analysis:
            {appTasksAnalysis}
            
            Output:
            
            Return the page analysis as valid JSON. Do not wrap the JSON in Markdown code blocks or prose.
            
            JSON Structure:
            {
            	"name": "string",
            	"purpose": "string",
            	"url": "string",
            	"elements": [
            		{
            			"id": "EL_NUMERICAL_ID",
            			"role": "BUTTON" | "LINK" | "TEXTBOX" | "HEADING" | "PARAGRAPH" | "CHECKBOX" | "RADIO" | "SWITCH" | "COMBOBOX" | "OPTION",
            			"attributes": {
            					"content": "string",
            					"id": "string",
            					"className": "string",
            					"placeholder": "string"
            			}
            			"href": "string"
            			"supportedActions": ["CLICK" | "FILL" | "CHECK" | "UNCHECK" | "SELECTOPTION"]
            		}
            	],
            	"possibleActions": [
            		{
            			"id": "PA_NUMERICAL_ID",
            			"name": "string",
            			"description": "string",
            			"requiredElements": [ "EL_NUMERICAL_ID" ],
            		}
            	]
            }
            """)
    AnalyzedAppPageDto analyze(
            @V("pageDocument") String pageDocument,
            @V("appIdeaAnalysis") AnalyzedAppIdeaDto appIdeaAnalysis,
            @V("appTasksAnalysis") List<AnalyzedAppTaskDto> appTasksAnalysis);
}
