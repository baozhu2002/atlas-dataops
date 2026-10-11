package io.baozhu.atlas.domain;

import java.util.*;

public final class DagValidator {
    private DagValidator() {}

    public static DagValidationResult validate(PipelineDefinition pipeline) {
        List<String> errors = new ArrayList<>();
        Map<String, TaskDefinition> byKey = new LinkedHashMap<>();
        for (TaskDefinition task : pipeline.tasks()) {
            if (byKey.putIfAbsent(task.key(), task) != null) errors.add("Duplicate task key: " + task.key());
        }
        for (TaskDefinition task : pipeline.tasks()) {
            for (String dependency : task.dependsOn()) {
                if (!byKey.containsKey(dependency)) errors.add("Unknown dependency " + dependency + " for task " + task.key());
                if (task.key().equals(dependency)) errors.add("Task cannot depend on itself: " + task.key());
            }
        }
        List<String> order = topologicalOrder(byKey);
        if (order.size() != byKey.size()) errors.add("Pipeline contains a dependency cycle");
        return new DagValidationResult(errors.isEmpty(), List.copyOf(errors), List.copyOf(order));
    }

    private static List<String> topologicalOrder(Map<String, TaskDefinition> byKey) {
        Map<String, Integer> incoming = new LinkedHashMap<>();
        Map<String, List<String>> outgoing = new HashMap<>();
        byKey.keySet().forEach(k -> { incoming.put(k, 0); outgoing.put(k, new ArrayList<>()); });
        for (TaskDefinition task : byKey.values()) {
            for (String dep : task.dependsOn()) {
                if (!byKey.containsKey(dep)) continue;
                incoming.compute(task.key(), (k, v) -> v + 1);
                outgoing.get(dep).add(task.key());
            }
        }
        ArrayDeque<String> queue = new ArrayDeque<>();
        incoming.forEach((key, count) -> { if (count == 0) queue.add(key); });
        List<String> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            String key = queue.remove(); order.add(key);
            for (String child : outgoing.get(key)) if (incoming.compute(child, (k, v) -> v - 1) == 0) queue.add(child);
        }
        return order;
    }
}
