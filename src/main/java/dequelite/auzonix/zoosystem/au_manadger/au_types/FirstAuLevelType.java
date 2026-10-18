package dequelite.auzonix.zoosystem.au_manadger.au_types;

import dequelite.auzonix.zoosystem.au_manadger.au_types.dto.AuTypeDto;
import org.springframework.stereotype.Component;

@Component
public class FirstAuLevelType implements IAuType {
    @Override
    public AuTypeDto getAuType(){
        return new AuTypeDto(
                "First AU Level",
                "Expert User",
                "AU_LEVEL_1",
                "Fully understands the application's purpose, UI layout, navigation, and input requirements. Knows exactly what to do and in what order.",
                """
                                - Act efficiently and precisely: perform only the actions needed to achieve the goal.\s
                          		* Fill fields in the logical order they appear on the page.
                          		* Use valid, correctly formatted values for every field\s
                          		* Do not make mistakes, do not re-enter data, do not click anything prematurely.\s
                          		* Do not perform redundant or exploratory actions.
                        """,
                """
                        * Keep the scenario between 2 and 7 steps.
                        * Every FILL step must contain a valid, non-empty value.\s
                        * Each element is used at most once.
                        """,
                """
                        * The scenario MUST end with the main flow completed: every required field is filled with a valid value and the final submit action is performed.
                        """
        );
    }
}
