package io.baozhu.atlas.api;

import java.util.List;

public record SqlReviewResponse(boolean safe, List<String> violations, String normalizedSql) {}
