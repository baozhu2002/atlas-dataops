package io.baozhu.atlas.domain;

import java.util.List;
import java.util.Map;

public record PipelineDefinition(String pipelineKey, String name, String version,
                                 List<TaskDefinition> tasks, Map<String, String> tags) {
    public PipelineDefinition {
        if (pipelineKey == null || pipelineKey.isBlank()) throw new IllegalArgumentException("pipelineKey is required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name is required");
        if (version == null || version.isBlank()) throw new IllegalArgumentException("version is required");
        if (tasks == null || tasks.isEmpty()) throw new IllegalArgumentException("At least one task is required");
        tasks = List.copyOf(tasks);
        tags = tags == null ? Map.of() : Map.copyOf(tags);
    }
}
