package dequelite.auzonix.zoosystem.test_action_collector;

import dequelite.auzonix.zoosystem.test_action_collector.dto.MonkeyTestActionDto;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TestActionCollectorService {

    private final ConcurrentHashMap<String, List<MonkeyTestActionDto>> testsActionsMap = new ConcurrentHashMap<>();

    public void addAction(MonkeyTestActionDto monkeyTestAction) {
        testsActionsMap.computeIfAbsent(
                monkeyTestAction.testId().toString(),
                test -> Collections.synchronizedList(new ArrayList<>())
        ).add(monkeyTestAction);
    }

    public synchronized Map<String, List<MonkeyTestActionDto>> getActions() {
        Map<String, List<MonkeyTestActionDto>> results = new HashMap<>();

        this.testsActionsMap.forEach((testId, actions) -> {
            results.put(testId, new ArrayList<>(actions));
        });

        return results;
    }
}
