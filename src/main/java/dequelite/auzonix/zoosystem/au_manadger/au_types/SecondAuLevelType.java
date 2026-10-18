package dequelite.auzonix.zoosystem.au_manadger.au_types;

import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import org.springframework.stereotype.Component;

@Component
public class SecondAuLevelType implements IAuType {
    @Override
    public AuTypeDto getAuType(){
        return new AuTypeDto(
                "Second AU Level",
                "First Look User",
                "AU_LEVEL_2",
                "Understands the application's overall purpose, but gets confused by the UI layout, navigation, and input requirements",
                """
                        		- DO NOT make the intermediate steps perfect.
                        		- Include 1-3 realistic human mistakes: invalid or incomplete entries, premature submit, wrong format, skipped fields, re-entering data, or actions out of logical order.
                        		- Mistakes may be made only on elements that exist in the page structure.
                        		* Every mistake must be followed by a recovery: the user notices the problem and corrects it (re-fills the field with a valid value, fills the skipped field, or retries the action).
                        """,
                """
                        		- Keep the scenario between 5 and 12 steps.
                        		- Each mistake must be plausible for the profile and must follow naturally from the confusion described in "domainKnowledge" (for example, a wrong format because the field requirements are unclear), not random or absurd input.
                        		- Mistakes are allowed only as intermediate steps.
                        		- Do not end the scenario on a mistake. The last step must be the logical completion of the goal.
                        """,
                """
                        		- The final state of every required field must be valid.
                        		- The scenario MUST end with the user completing the main flow: all required fields are filled with valid values and the final submit action is performed.
                        """
        );
    }
}
