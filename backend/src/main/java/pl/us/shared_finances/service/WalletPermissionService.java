package pl.us.shared_finances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.entity.Membership;
import pl.us.shared_finances.entity.enums.WalletRole;
import pl.us.shared_finances.repository.MembershipRepository;

@Service
@RequiredArgsConstructor
public class WalletPermissionService {

    private final MembershipRepository membershipRepository;

    public Membership requireMember(Long walletId, Long userId) {
        return membershipRepository.findByWalletIdAndUserId(walletId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "WALLET_NOT_FOUND"));
    }

    public Membership requireEditor(Long walletId, Long userId) {
        Membership membership = requireMember(walletId, userId);

        if (membership.getRole() == WalletRole.GUEST) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "GUEST_READ_ONLY");
        }
        return membership;
    }

    public Membership requireOwner(Long walletId, Long userId) {
        Membership membership = requireMember(walletId, userId);

        if (membership.getRole() != WalletRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "NOT_OWNER");
        }
        return membership;
    }
}