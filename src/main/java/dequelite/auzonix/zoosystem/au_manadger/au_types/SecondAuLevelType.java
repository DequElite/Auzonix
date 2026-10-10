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
                "Understands the application's overall purpose, but gets confused by the UI layout, navigation, and input requirements"
        );
    }
}
