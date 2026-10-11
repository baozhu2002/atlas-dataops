package io.baozhu.atlas.execution;
import io.baozhu.atlas.domain.*; import org.springframework.stereotype.Component;
@Component public class GenericTaskExecutor implements TaskExecutor { public TaskType supports(){return TaskType.HTTP;} public TaskExecutionResult execute(TaskExecutionContext c){long rows=Long.parseLong(c.task().config().getOrDefault("rows","1"));return new TaskExecutionResult(rows,rows,""+c.task().type()+" task completed");} }
