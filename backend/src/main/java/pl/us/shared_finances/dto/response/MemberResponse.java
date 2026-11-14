package pl.us.shared_finances.dto.response;

import pl.us.shared_finances.entity.enums.WalletRole;

public record MemberResponse(
        Long id,
        String displayName,
        String email,
        WalletRole role
) {
}