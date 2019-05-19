package com.slyak.license;

import com.slyak.spring.jpa.GenericJpaRepositoryFactoryBean;
import com.slyak.spring.jpa.GenericJpaRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.slyak"})
@EnableCaching(proxyTargetClass = true)
@EnableJpaAuditing
@EnableAsync
@EnableJpaRepositories(basePackages = "com.slyak.license.repository", repositoryFactoryBeanClass = GenericJpaRepositoryFactoryBean.class, repositoryBaseClass = GenericJpaRepositoryImpl.class)
@EntityScan({"com.slyak"})
@ComponentScan("com.slyak")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
