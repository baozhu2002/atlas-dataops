package io.baozhu.atlas.persistence;

import io.baozhu.atlas.domain.PipelineDefinition;
import java.time.LocalDateTime;

public record StoredPipeline(Long id, PipelineDefinition definition, LocalDateTime createdAt) {}
