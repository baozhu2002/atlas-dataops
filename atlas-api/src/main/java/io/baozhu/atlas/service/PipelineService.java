package io.baozhu.atlas.service;
import io.baozhu.atlas.api.*; import io.baozhu.atlas.domain.*; import io.baozhu.atlas.persistence.*; import org.springframework.stereotype.Service; import java.util.*;
@Service public class PipelineService { private final PipelineRepository repo; public PipelineService(PipelineRepository r){repo=r;}
 public PipelineResponse create(CreatePipelineRequest r){PipelineDefinition p=new PipelineDefinition(r.pipelineKey(),r.name(),r.version(),r.tasks().stream().map(t->new TaskDefinition(t.key(),t.name(),TaskType.valueOf(t.type().toUpperCase()),t.dependsOn(),t.config(),t.maxRetries()==null?0:t.maxRetries())).toList(),r.tags());DagValidationResult result=DagValidator.validate(p);if(!result.valid())throw new IllegalArgumentException(String.join("; ",result.errors()));StoredPipeline saved=repo.save(p);return new PipelineResponse(saved.id(),saved.definition(),saved.createdAt());}
 public PipelineResponse get(Long id){StoredPipeline p=repo.findById(id);return new PipelineResponse(p.id(),p.definition(),p.createdAt());}
 public List<PipelineResponse> list(){return repo.findAll().stream().map(p->new PipelineResponse(p.id(),p.definition(),p.createdAt())).toList();}
}
