package com.bet.betwebservice.config;

import com.bet.betwebservice.entity.PodEntity;
import com.bet.betwebservice.entity.StampEntity;
import com.bet.betwebservice.entity.TaskEntity;
import com.bet.betwebservice.entity.UserEntity;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {

    private String theAllowedOrigins = "http://localhost:3000";

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config,
            CorsRegistry cors
    ) {
        HttpMethod[] theUnsupportedActions = {HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.PUT};
        config.exposeIdsFor(PodEntity.class);
        config.exposeIdsFor(StampEntity.class);
        config.exposeIdsFor(TaskEntity.class);
        config.exposeIdsFor(UserEntity.class);

        disableHttpMethods(PodEntity.class, config, theUnsupportedActions);
        disableHttpMethods(StampEntity.class, config, theUnsupportedActions);
        disableHttpMethods(TaskEntity.class, config, theUnsupportedActions);
        disableHttpMethods(UserEntity.class, config, theUnsupportedActions);

        /* Configure CORS Mapping */
        cors.addMapping(config.getBasePath() + "/**")
                .allowedOrigins(theAllowedOrigins);
    }

    private void disableHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
        config.getExposureConfiguration()
                .forDomainType(theClass)
                .withItemExposure((metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
                .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
    }
}
