package io.baozhu.atlas.domain;

import java.util.*;

public final class LineageGraph {
    public record Node(String id, String kind) {}
    public record Edge(String from, String to, String relation) {}
    private final Set<Node> nodes = new LinkedHashSet<>(); private final Set<Edge> edges = new LinkedHashSet<>();
    public static LineageGraph from(PipelineDefinition pipeline) {
        LineageGraph graph = new LineageGraph();
        for (TaskDefinition task : pipeline.tasks()) {
            graph.nodes.add(new Node(task.key(), "task"));
            String output = task.config().get("output");
            if (output != null) { graph.nodes.add(new Node(output, "dataset")); graph.edges.add(new Edge(task.key(), output, "produces")); }
            String input = task.config().get("input");
            if (input != null) { graph.nodes.add(new Node(input, "dataset")); graph.edges.add(new Edge(input, task.key(), "consumes")); }
            for (String dep : task.dependsOn()) graph.edges.add(new Edge(dep, task.key(), "depends_on"));
        }
        return graph;
    }
    public Set<Node> nodes() { return Set.copyOf(nodes); }
    public Set<Edge> edges() { return Set.copyOf(edges); }
}
