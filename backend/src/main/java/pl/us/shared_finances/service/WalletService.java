package pl.us.shared_finances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.dto.request.WalletRequest;
import pl.us.shared_finances.dto.response.InvitationResponse;
import pl.us.shared_finances.dto.response.MemberResponse;
import pl.us.shared_finances.dto.response.WalletResponse;
import pl.us.shared_finances.entity.*;
import pl.us.shared_finances.entity.enums.InvitationStatus;
import pl.us.shared_finances.entity.enums.WalletRole;
import pl.us.shared_finances.repository.InvitationRepository;
import pl.us.shared_finances.repository.MembershipRepository;
import pl.us.shared_finances.repository.UserRepository;
import pl.us.shared_finances.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final MembershipRepository membershipRepository;
    private final InvitationRepository invitationRepository;
    private final UserRepository userRepository;
    private final WalletPermissionService permissionService;

    // POST /api/wallets
    @Transactional
    public WalletResponse create(Long userId, WalletRequest request) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "USER_NOT_FOUND"));

        return createWallet(owner, request.name());
    }

    @Transactional
    public WalletResponse createWallet(User owner, String name) {
        Wallet wallet = new Wallet();
        wallet.setName(name.trim());
        walletRepository.save(wallet);

        Membership membership = new Membership();
        membership.setWallet(wallet);
        membership.setUser(owner);
        membership.setRole(WalletRole.OWNER);
        membershipRepository.save(membership);

        return toResponse(wallet, WalletRole.OWNER);
    }

    // GET /api/wallets
    @Transactional(readOnly = true)
    public List<WalletResponse> getMyWallets(Long userId) {
        List<WalletResponse> result = new ArrayList<>();

        for (Membership membership : membershipRepository.findByUserId(userId)) {
            result.add(toResponse(membership.getWallet(), membership.getRole()));
        }
        return result;
    }

    // GET /api/wallets/{walletId}
    @Transactional(readOnly = true)
    public WalletResponse getWallet(Long walletId, Long userId) {
        Membership membership = permissionService.requireMember(walletId, userId);
        return toResponse(membership.getWallet(), membership.getRole());
    }

    // PUT /api/wallets/{walletId}
    @Transactional
    public WalletResponse renameWallet(Long walletId, Long userId, WalletRequest request) {
        Membership membership = permissionService.requireOwner(walletId, userId);

        Wallet wallet = membership.getWallet();
        wallet.setName(request.name().trim());
        walletRepository.save(wallet);

        return toResponse(wallet, membership.getRole());
    }

    // DELETE /api/wallets/{walletId}
    @Transactional
    public void deleteWallet(Long walletId, Long userId) {
        permissionService.requireOwner(walletId, userId);
        walletRepository.deleteById(walletId);
    }

    private WalletResponse toResponse(Wallet wallet, WalletRole myRole) {
        List<MemberResponse> members = new ArrayList<>();
        String ownerName = null;

        for (Membership membership : membershipRepository.findByWalletId(wallet.getId())) {
            User user = membership.getUser();
            members.add(new MemberResponse(user.getId(), user.getDisplayName(), user.getEmail(), membership.getRole()));

            if (membership.getRole() == WalletRole.OWNER) {
                ownerName = user.getDisplayName();
            }
        }

        // oczekujące zaproszenia widzi tylko właściciel
        List<InvitationResponse> invitations = new ArrayList<>();
        if (myRole == WalletRole.OWNER) {
            for (Invitation invitation : invitationRepository.findByWalletIdAndStatus(wallet.getId(), InvitationStatus.PENDING)) {
                invitations.add(new InvitationResponse(
                        invitation.getId(),
                        wallet.getId(),
                        wallet.getName(),
                        ownerName,
                        invitation.getInvitee().getEmail(),
                        invitation.getRole(),
                        invitation.getCreatedAt()
                ));
            }
        }

        return new WalletResponse(
                wallet.getId(),
                wallet.getName(),
                myRole,
                BigDecimal.ZERO, // TODO Szymon: saldo z transakcji
                BigDecimal.ZERO, // TODO Szymon: wydatki w bieżącym miesiącu
                wallet.getMonthlyLimit(),
                wallet.getWarningThreshold(),
                members,
                invitations
        );
    }
}