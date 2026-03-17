package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.repository;

import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.entity.TelegramUserEntity;
import org.apache.catalina.mapper.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TelegramUserJpaRepository extends JpaRepository<TelegramUserEntity, Long> {
    Optional<TelegramUserEntity> findByTelegramId(Long telegramId);
}
