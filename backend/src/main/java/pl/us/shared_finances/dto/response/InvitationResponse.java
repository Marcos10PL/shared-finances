package pl.us.shared_finances.dto.response;

import pl.us.shared_finances.entity.enums.WalletRole;

import java.time.LocalDateTime;

public record InvitationResponse(
        Long id,
        Long walletId,
        String walletName,
        String ownerName, // kto zaprasza
        String email,     // kogo zaproszono
        WalletRole role,
        LocalDateTime createdAt
) {
}