package io.baozhu.atlas.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public record CreatePipelineRequest(@NotBlank String pipelineKey, @NotBlank String name, @NotBlank String version,
                                    @NotEmpty List<@Valid TaskRequest> tasks, Map<String,String> tags) {
    public record TaskRequest(@NotBlank String key, @NotBlank String name, @NotBlank String type,
                              List<String> dependsOn, Map<String,String> config, Integer maxRetries) {}
}
