package io.baozhu.atlas.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.baozhu.atlas.domain.PipelineDefinition;
import io.baozhu.atlas.common.NotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.List;

@Repository
public class PipelineRepository {
    private final JdbcTemplate jdbc; private final ObjectMapper mapper;
    public PipelineRepository(JdbcTemplate jdbc,ObjectMapper mapper){this.jdbc=jdbc;this.mapper=mapper;}
    public StoredPipeline save(PipelineDefinition definition){
        try { KeyHolder keys=new GeneratedKeyHolder(); String json=mapper.writeValueAsString(definition);
            jdbc.update(c->{var ps=c.prepareStatement("INSERT INTO pipeline(pipeline_key,name,version,definition_json) VALUES (?,?,?,?)",java.sql.Statement.RETURN_GENERATED_KEYS);ps.setString(1,definition.pipelineKey());ps.setString(2,definition.name());ps.setString(3,definition.version());ps.setString(4,json);return ps;},keys);
            return findById(((Number)keys.getKeys().get("ID")).longValue());
        } catch(Exception e){throw new IllegalStateException("Could not persist pipeline",e);}
    }
    public StoredPipeline findById(Long id){return jdbc.query("SELECT * FROM pipeline WHERE id=?",this::map,id).stream().findFirst().orElseThrow(()->new NotFoundException("Pipeline not found: "+id));}
    public List<StoredPipeline> findAll(){return jdbc.query("SELECT * FROM pipeline ORDER BY id DESC",this::map);}
    private StoredPipeline map(ResultSet rs,int row)throws java.sql.SQLException{try{return new StoredPipeline(rs.getLong("id"),mapper.readValue(rs.getString("definition_json"),PipelineDefinition.class),rs.getTimestamp("created_at").toLocalDateTime());}catch(Exception e){throw new java.sql.SQLException("Invalid pipeline JSON",e);}}
}
