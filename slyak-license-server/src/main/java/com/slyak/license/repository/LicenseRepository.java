package com.slyak.license.repository;

import com.slyak.license.domain.License;
import com.slyak.license.domain.LicenseQuery;
import com.slyak.spring.jpa.GenericJpaRepository;
import com.slyak.spring.jpa.TemplateQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LicenseRepository extends GenericJpaRepository<License, String> {
    @TemplateQuery
    Page<License> query(LicenseQuery licenseQuery, Pageable pageable);
}