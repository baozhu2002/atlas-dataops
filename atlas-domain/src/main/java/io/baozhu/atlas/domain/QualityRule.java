package io.baozhu.atlas.domain;

public record QualityRule(String field, QualityRuleType type, String argument, QualitySeverity severity) {
    public QualityRule { if (field == null || field.isBlank()) throw new IllegalArgumentException("Quality field is required"); if (type == null) throw new IllegalArgumentException("Quality type is required"); severity = severity == null ? QualitySeverity.ERROR : severity; }
}
