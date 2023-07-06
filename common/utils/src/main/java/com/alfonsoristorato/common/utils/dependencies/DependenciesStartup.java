package com.alfonsoristorato.common.utils.dependencies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.actuate.health.*;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Set;
@Component
public class DependenciesStartup {
    private final Logger log = LoggerFactory.getLogger(DependenciesStartup.class);
    private static final Set<String> HEALTH_PATHS = Set.of("Kafka", "SignatureVerifier");

    //TODO: this results in 2 messages being created (at bean creation and at method call) - find a better way
    @Bean
    public ApplicationRunner healthCheckRunner(HealthEndpoint healthEndpoint, ConfigurableApplicationContext ctx) {
        return args -> {
            HealthComponent health = healthEndpoint.health();

            if (health.getStatus() == Status.DOWN) {
                if(health instanceof CompositeHealth compositeHealth) {
                    HEALTH_PATHS
                            .stream()
                            .filter(path -> compositeHealth.getComponents().containsKey(path) && compositeHealth.getComponents().get(path).getStatus().equals(Status.DOWN))
                            .forEach(healthComponent ->
                                    log.error("{} health is DOWN.", healthComponent));
                }
                log.error("Application health is DOWN. Stopping the application...");
                ctx.close();
            }
        };
    }
}
