package io.baozhu.atlas.domain;

import java.util.Map;
import java.util.Set;

public final class RunStateMachine {
    private static final Map<RunStatus, Set<RunStatus>> ALLOWED = Map.of(
            RunStatus.PENDING, Set.of(RunStatus.RUNNING, RunStatus.CANCELLED),
            RunStatus.RUNNING, Set.of(RunStatus.SUCCESS, RunStatus.FAILED, RunStatus.CANCELLED),
            RunStatus.SUCCESS, Set.of(), RunStatus.FAILED, Set.of(), RunStatus.CANCELLED, Set.of());
    private RunStateMachine() {}
    public static void ensureTransition(RunStatus from, RunStatus to) {
        if (!ALLOWED.getOrDefault(from, Set.of()).contains(to)) throw new IllegalStateException("Illegal run transition: " + from + " -> " + to);
    }
}
