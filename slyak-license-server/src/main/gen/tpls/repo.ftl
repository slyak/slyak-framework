package com.slyak.license.repository;

import com.slyak.license.domain.${entity.name};
import com.slyak.license.domain.${entity.name}Query;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ${entity.name}Repository extends GenericJpaRepository<${entity.name}, ${entity.idClass}> {
@TemplateQuery
Page<${entity.name}> query(${entity.name}Query ${entity.name?uncap_first}Query, Pageable pageable);
}