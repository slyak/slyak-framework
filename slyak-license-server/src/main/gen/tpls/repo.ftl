package com.slyak.license.repository;

import com.slyak.license.domain.${entity.name};
import com.slyak.license.domain.${entity.name}Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ${entity.name}Repository extends JpaRepository<${entity.name}, ${entity.idClass}> {
}