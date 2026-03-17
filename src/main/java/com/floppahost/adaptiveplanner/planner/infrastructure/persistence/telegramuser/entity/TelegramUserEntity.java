package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.entity;

import com.floppahost.adaptiveplanner.planner.domain.telegram.ChatState;
import com.floppahost.adaptiveplanner.planner.infrastructure.persistence.common.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@Entity
@NoArgsConstructor
@Table(name = "telegram_users")
public class TelegramUserEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long telegramId;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(nullable = false)
    private Long chatId;

    @Column(name = "chat_state")
    @Enumerated(EnumType.STRING)
    private ChatState state = ChatState.IDLE;

    private String statePayload;

    public TelegramUserEntity(Long telegramId, UUID userId, Long chatId, ChatState state, String statePayload) {
        this.telegramId = telegramId;
        this.userId = userId;
        this.chatId = chatId;
        this.state = state;
        this.statePayload = statePayload;
    }
}
