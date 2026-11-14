package pl.us.shared_finances.dto.response;

import pl.us.shared_finances.entity.enums.WalletRole;

import java.math.BigDecimal;
import java.util.List;

public record WalletResponse(
        Long id,
        String name,
        WalletRole role,                     // rola zalogowanego usera
        BigDecimal balance,
        BigDecimal monthlyExpenses,
        BigDecimal monthlyLimit,
        Integer warningThreshold,
        List<MemberResponse> members,
        List<InvitationResponse> invitations // oczekujące, wypełniamy tylko właścicielowi
) {
}