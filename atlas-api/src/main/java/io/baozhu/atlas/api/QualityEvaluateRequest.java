package io.baozhu.atlas.api;

import io.baozhu.atlas.domain.QualityRule;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

public record QualityEvaluateRequest(List<Map<String,Object>> rows, @NotEmpty List<@Valid QualityRule> rules) {}
