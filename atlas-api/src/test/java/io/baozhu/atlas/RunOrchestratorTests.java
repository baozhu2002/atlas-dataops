package io.baozhu.atlas;

import io.baozhu.atlas.api.CreatePipelineRequest;
import io.baozhu.atlas.api.PipelineResponse;
import io.baozhu.atlas.api.RunResponse;
import io.baozhu.atlas.service.PipelineService;
import io.baozhu.atlas.service.RunOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RunOrchestratorTests {
    @Autowired PipelineService pipelines;
    @Autowired RunOrchestrator runs;

    @Test
    void executesDagWithRetriesAndIdempotency() {
        PipelineResponse pipeline = pipelines.create(new CreatePipelineRequest(
                "test-orders", "Test Orders", "1",
                List.of(
                        new CreatePipelineRequest.TaskRequest("extract", "Extract", "SQL", List.of(), Map.of("rows", "10"), 1),
                        new CreatePipelineRequest.TaskRequest("publish", "Publish", "HTTP", List.of("extract"), Map.of("rows", "2"), 0)),
                Map.of("test", "true")));
        RunResponse first = runs.start(pipeline.id(), "integration-run-1");
        RunResponse second = runs.start(pipeline.id(), "integration-run-1");
        assertThat(first.status().name()).isEqualTo("SUCCESS");
        assertThat(second.id()).isEqualTo(first.id());
        assertThat(first.summary()).containsEntry("completedTasks", List.of("extract", "publish"));
    }
}
