package io.baozhu.atlas.domain;

import java.util.List;
import java.util.Map;

public record TaskDefinition(String key, String name, TaskType type, List<String> dependsOn,
                             Map<String, String> config, int maxRetries) {
    public TaskDefinition {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("Task key is required");
        if (type == null) throw new IllegalArgumentException("Task type is required: " + key);
        dependsOn = dependsOn == null ? List.of() : List.copyOf(dependsOn);
        config = config == null ? Map.of() : Map.copyOf(config);
        if (maxRetries < 0 || maxRetries > 10) throw new IllegalArgumentException("maxRetries must be between 0 and 10");
    }
}
