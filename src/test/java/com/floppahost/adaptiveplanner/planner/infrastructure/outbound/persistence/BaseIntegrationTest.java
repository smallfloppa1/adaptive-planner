package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaTestConfig.class)
public abstract class BaseIntegrationTest {
}
