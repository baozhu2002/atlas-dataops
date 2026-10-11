package io.baozhu.atlas.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;

public record SqlReviewRequest(@NotBlank String sql, @NotEmpty Set<@NotBlank String> allowedTables) {}
