package com.alfonsoristorato.bookmarksproducer.app.dependencies;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DependenciesStartupTest {
    @InjectMocks
    private DependenciesStartup dependenciesStartup;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HealthEndpoint healthEndpoint;

    @Mock
    private static ConfigurableApplicationContext ctx;

    @Mock
    private ApplicationArguments applicationArguments;

    @Test
    public void healthCheckRunner_shouldCloseContextIfHealthIsDown() throws Exception {
        when(healthEndpoint.health().getStatus()).thenReturn(Status.DOWN);

        dependenciesStartup.healthCheckRunner(healthEndpoint, ctx).run(applicationArguments);

        verify(ctx).close();
    }

    @Test
    public void healthCheckRunner_shouldNotCloseContextIfHealthIsUp() throws Exception {
        when(healthEndpoint.health().getStatus()).thenReturn(Status.UP);

        dependenciesStartup.healthCheckRunner(healthEndpoint, ctx).run(applicationArguments);

        verifyNoInteractions(ctx);
    }

}
