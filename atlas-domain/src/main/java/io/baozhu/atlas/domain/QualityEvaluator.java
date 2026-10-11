package io.baozhu.atlas.domain;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

public final class QualityEvaluator {
    public List<QualityResult> evaluate(List<Map<String, Object>> rows, List<QualityRule> rules) {
        List<Map<String,Object>> safeRows = rows == null ? List.of() : rows;
        return rules.stream().map(rule -> evaluateOne(safeRows, rule)).toList();
    }
    private QualityResult evaluateOne(List<Map<String,Object>> rows, QualityRule rule) {
        long failed;
        switch (rule.type()) {
            case NOT_NULL -> failed = rows.stream().filter(r -> r.get(rule.field()) == null).count();
            case UNIQUE -> failed = rows.size() - rows.stream().map(r -> String.valueOf(r.get(rule.field()))).distinct().count();
            case NON_NEGATIVE -> failed = rows.stream().filter(r -> negative(r.get(rule.field()))).count();
            case REGEX -> { Pattern p = Pattern.compile(rule.argument()); failed = rows.stream().filter(r -> r.get(rule.field()) != null && !p.matcher(String.valueOf(r.get(rule.field()))).matches()).count(); }
            case ROW_COUNT_MIN -> { long min = Long.parseLong(rule.argument()); failed = rows.size() < min ? min - rows.size() : 0; }
            default -> throw new IllegalArgumentException("Unsupported rule: " + rule.type());
        }
        return new QualityResult(rule, rows.size(), failed, failed == 0, failed == 0 ? "passed" : failed + " violation(s)");
    }
    private boolean negative(Object value) { if (value == null) return false; try { return new BigDecimal(value.toString()).signum() < 0; } catch (NumberFormatException e) { return true; } }
}
