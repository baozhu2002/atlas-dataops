package io.baozhu.atlas.api;

import io.baozhu.atlas.domain.PipelineDefinition;
import java.time.LocalDateTime;

public record PipelineResponse(Long id, PipelineDefinition definition, LocalDateTime createdAt) {}
