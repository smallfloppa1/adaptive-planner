package com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.telegram.entity;

import com.floppahost.adaptiveplanner.planner.infrastructure.outbound.persistence.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "telegram_user")
@EntityListeners(AuditingEntityListener.class)
public class TelegramUserEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramUserId;

    @Column(nullable = false)
    private Long chatId;

    public TelegramUserEntity(Long telegramUserId, Long chatId) {
        this.telegramUserId = telegramUserId;
        this.chatId = chatId;
    }
}
