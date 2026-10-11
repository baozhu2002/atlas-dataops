package io.baozhu.atlas.api;

import io.baozhu.atlas.domain.QualityResult;
import java.util.List;

public record QualityEvaluateResponse(boolean passed, long checkedRows, List<QualityResult> results) {}
