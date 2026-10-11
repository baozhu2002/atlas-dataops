package io.baozhu.atlas.api;

import io.baozhu.atlas.domain.RunStatus;
import java.time.LocalDateTime;
import java.util.Map;

public record RunResponse(Long id, String runKey, Long pipelineId, RunStatus status, Map<String,Object> summary,
                          LocalDateTime startedAt, LocalDateTime finishedAt) {}
