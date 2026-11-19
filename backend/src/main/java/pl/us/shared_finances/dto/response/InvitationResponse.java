package pl.us.shared_finances.dto.response;

import pl.us.shared_finances.entity.enums.WalletRole;

import java.time.LocalDateTime;

public record InvitationResponse(
        Long id,
        Long walletId,
        String walletName,
        Long ownerId,     // kto zaprasza
        String ownerName,
        String email,     // kogo zaproszono
        WalletRole role,
        LocalDateTime createdAt
) {
}