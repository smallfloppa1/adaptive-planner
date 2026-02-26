package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence;

import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles
@EnableJpaAuditing
public class JpaTestConfig {
}
