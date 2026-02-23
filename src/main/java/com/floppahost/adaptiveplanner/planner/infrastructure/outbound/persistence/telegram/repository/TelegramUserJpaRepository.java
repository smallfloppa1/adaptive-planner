package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.repository;

import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.entity.TelegramUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramUserJpaRepository extends JpaRepository<TelegramUserEntity, Long> {

    Optional<TelegramUserEntity> findByTelegramUserId(Long telegramUserId);
}
