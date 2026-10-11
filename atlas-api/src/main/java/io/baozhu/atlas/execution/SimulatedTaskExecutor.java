package io.baozhu.atlas.execution;
import io.baozhu.atlas.domain.*; import org.springframework.stereotype.Component;
@Component public class SimulatedTaskExecutor implements TaskExecutor {
 public TaskType supports(){return TaskType.SQL;}
 public TaskExecutionResult execute(TaskExecutionContext c){String mode=c.task().config().getOrDefault("failure_mode","");if("always".equalsIgnoreCase(mode))throw new IllegalStateException("Simulated SQL failure requested by pipeline config");long rows=Long.parseLong(c.task().config().getOrDefault("rows","100"));return new TaskExecutionResult(rows,rows,"SQL task completed");}
}
