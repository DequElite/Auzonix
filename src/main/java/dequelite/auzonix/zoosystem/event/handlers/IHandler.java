package dequelite.auzonix.zoosystem.event.handlers;

import dequelite.auzonix.zoosystem.event.dto.PublishedEventDto;

public interface IHandler {
    void handle(PublishedEventDto<?> event);
}
