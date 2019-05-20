package com.slyak.license.config;

import com.slyak.spring.jpa.GenericJpaRepositoryFactoryBean;
import com.slyak.spring.jpa.GenericJpaRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author by wolf on 2018/12/26.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.slyak",
		repositoryBaseClass = GenericJpaRepositoryImpl.class,
		repositoryFactoryBeanClass = GenericJpaRepositoryFactoryBean.class)
@EntityScan({ "com.slyak" })
@ComponentScan("com.slyak")
public class JpaConfig {
}
