package com.alfonsoristorato.bookmarksproducer.app.dependencies;

import com.alfonsoristorato.common.kafka.health.KafkaHealthIndicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DependenciesStartupTest {

    @InjectMocks
    private DependenciesStartup dependenciesStartup;

    @Mock
    private KafkaHealthIndicator kafkaHealthIndicator;


    @Test
    void shouldNotThrowException_ifAllStatusesAreUp() {
        when(kafkaHealthIndicator.health()).thenReturn(Mono.just(Health.up().build()));

        assertThatNoException().isThrownBy(() -> dependenciesStartup.onStartup());
    }
    @Test
    void shouldThrowException_ifAStatusIsDown() {
        when(kafkaHealthIndicator.health()).thenReturn(Mono.just(Health.down().build()));

        RuntimeException runtimeException = catchThrowableOfType(() -> dependenciesStartup.onStartup(), RuntimeException.class);

        assertThat(runtimeException).hasMessage("Kafka is down.");
    }


}
