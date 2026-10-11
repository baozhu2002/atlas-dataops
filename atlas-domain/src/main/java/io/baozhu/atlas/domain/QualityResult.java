package io.baozhu.atlas.domain;

public record QualityResult(QualityRule rule, long checkedRows, long failedRows, boolean passed, String message) {}
