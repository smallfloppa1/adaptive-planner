package com.floppahost.adaptiveplanner.planner.infrastructure.persistence.telegramuser.entity;

import com.floppahost.adaptiveplanner.planner.application.port.outbound.telegramuserrepository.dto.ChatState;
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
    private Long telegramUserId;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false, unique = true)
    private UUID userId;

    @Column(name = "chat_state")
    @Enumerated(EnumType.STRING)
    private ChatState state = ChatState.IDLE;

    private String statePayload;

    public TelegramUserEntity(Long telegramUserId, Long chatId, UUID userId) {
        this.telegramUserId = telegramUserId;
        this.chatId = chatId;
        this.userId = userId;
    }
}
