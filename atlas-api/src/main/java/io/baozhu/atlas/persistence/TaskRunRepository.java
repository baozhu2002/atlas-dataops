package io.baozhu.atlas.persistence;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TaskRunRepository {
    private final JdbcTemplate jdbc;
    public TaskRunRepository(JdbcTemplate jdbc){this.jdbc=jdbc;}
    public void start(Long runId,String taskKey){jdbc.update("INSERT INTO task_run(pipeline_run_id,task_key,status) VALUES (?,?,?)",runId,taskKey,"RUNNING");}
    public void success(Long runId,String taskKey,int attempts,long read,long written){jdbc.update("UPDATE task_run SET status='SUCCESS',attempts=?,records_read=?,records_written=?,finished_at=CURRENT_TIMESTAMP WHERE pipeline_run_id=? AND task_key=?",attempts,read,written,runId,taskKey);}
    public void failed(Long runId,String taskKey,int attempts,String error){jdbc.update("UPDATE task_run SET status='FAILED',attempts=?,error_message=?,finished_at=CURRENT_TIMESTAMP WHERE pipeline_run_id=? AND task_key=?",attempts,error,runId,taskKey);}
}
