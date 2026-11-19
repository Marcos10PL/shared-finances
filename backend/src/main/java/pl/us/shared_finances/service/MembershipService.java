package pl.us.shared_finances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.dto.request.ChangeRoleRequest;
import pl.us.shared_finances.dto.response.MemberResponse;
import pl.us.shared_finances.entity.Membership;
import pl.us.shared_finances.entity.User;
import pl.us.shared_finances.entity.enums.WalletRole;
import pl.us.shared_finances.repository.MembershipRepository;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final WalletPermissionService permissionService;

    // PUT /api/wallets/{walletId}/members/{memberId}/role
    @Transactional
    public MemberResponse changeRole(Long walletId, Long userId, Long memberId, ChangeRoleRequest request) {
        permissionService.requireOwner(walletId, userId);

        if (request.role() == WalletRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ROLE");
        }

        Membership member = findMember(walletId, memberId);

        if (member.getRole() == WalletRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OWNER_PROTECTED");
        }

        member.setRole(request.role());
        membershipRepository.save(member);
        return toResponse(member);
    }

    // DELETE /api/wallets/{walletId}/members/{memberId}
    @Transactional
    public void removeMember(Long walletId, Long userId, Long memberId) {
        permissionService.requireOwner(walletId, userId);

        Membership member = findMember(walletId, memberId);

        if (member.getRole() == WalletRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OWNER_PROTECTED");
        }

        membershipRepository.delete(member);
    }

    // POST /api/wallets/{walletId}/leave
    @Transactional
    public void leaveWallet(Long walletId, Long userId) {
        Membership membership = permissionService.requireMember(walletId, userId);

        if (membership.getRole() == WalletRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OWNER_CANNOT_LEAVE");
        }

        membershipRepository.delete(membership);
    }

    private Membership findMember(Long walletId, Long memberId) {
        return membershipRepository.findByWalletIdAndUserId(walletId, memberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "MEMBER_NOT_FOUND"));
    }

    private MemberResponse toResponse(Membership membership) {
        User user = membership.getUser();
        return new MemberResponse(user.getId(), user.getDisplayName(), user.getEmail(), membership.getRole());
    }
}