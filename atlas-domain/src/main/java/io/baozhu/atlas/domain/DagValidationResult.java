package io.baozhu.atlas.domain;

import java.util.List;

public record DagValidationResult(boolean valid, List<String> errors, List<String> topologicalOrder) {}
