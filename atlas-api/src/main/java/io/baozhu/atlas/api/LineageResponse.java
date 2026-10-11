package io.baozhu.atlas.api;

import io.baozhu.atlas.domain.LineageGraph;
import java.util.Set;

public record LineageResponse(Set<LineageGraph.Node> nodes, Set<LineageGraph.Edge> edges) {}
