package io.baozhu.atlas.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.baozhu.atlas.api.RunResponse;
import io.baozhu.atlas.domain.RunStatus;
import io.baozhu.atlas.common.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class RunRepository {
    private final JdbcTemplate jdbc; private final ObjectMapper mapper;
    public RunRepository(JdbcTemplate jdbc,ObjectMapper mapper){this.jdbc=jdbc;this.mapper=mapper;}
    public RunResponse create(String runKey,Long pipelineId){KeyHolder keys=new GeneratedKeyHolder();jdbc.update(c->{var ps=c.prepareStatement("INSERT INTO pipeline_run(run_key,pipeline_id,status) VALUES (?,?,?)",java.sql.Statement.RETURN_GENERATED_KEYS);ps.setString(1,runKey);ps.setLong(2,pipelineId);ps.setString(3,RunStatus.PENDING.name());return ps;},keys);return findById(((Number)keys.getKeys().get("ID")).longValue());}
    public RunResponse findByKey(String key){return jdbc.query("SELECT * FROM pipeline_run WHERE run_key=?",this::map,key).stream().findFirst().orElse(null);}
    public RunResponse findById(Long id){return jdbc.query("SELECT * FROM pipeline_run WHERE id=?",this::map,id).stream().findFirst().orElseThrow(()->new NotFoundException("Run not found: "+id));}
    public void transition(Long id,RunStatus from,RunStatus to,Map<String,Object> summary){io.baozhu.atlas.domain.RunStateMachine.ensureTransition(from,to);try{jdbc.update("UPDATE pipeline_run SET status=?,summary_json=?,finished_at=CASE WHEN ? IN ('SUCCESS','FAILED','CANCELLED') THEN CURRENT_TIMESTAMP ELSE finished_at END WHERE id=?",to.name(),mapper.writeValueAsString(summary),to.name(),id);}catch(Exception e){throw new IllegalStateException(e);}}
    private RunResponse map(ResultSet rs,int row)throws java.sql.SQLException{try{String json=rs.getString("summary_json");Map<String,Object> s=json==null?Map.of():mapper.readValue(json,new TypeReference<>(){});return new RunResponse(rs.getLong("id"),rs.getString("run_key"),rs.getLong("pipeline_id"),RunStatus.valueOf(rs.getString("status")),s,rs.getTimestamp("started_at").toLocalDateTime(),rs.getTimestamp("finished_at")==null?null:rs.getTimestamp("finished_at").toLocalDateTime());}catch(Exception e){throw new java.sql.SQLException("Invalid run row",e);}}
}
