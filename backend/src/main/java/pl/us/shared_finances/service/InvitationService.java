package pl.us.shared_finances.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pl.us.shared_finances.dto.request.InvitationRequest;
import pl.us.shared_finances.dto.response.InvitationResponse;
import pl.us.shared_finances.entity.Invitation;
import pl.us.shared_finances.entity.Membership;
import pl.us.shared_finances.entity.User;
import pl.us.shared_finances.entity.Wallet;
import pl.us.shared_finances.entity.enums.InvitationStatus;
import pl.us.shared_finances.entity.enums.WalletRole;
import pl.us.shared_finances.repository.InvitationRepository;
import pl.us.shared_finances.repository.MembershipRepository;
import pl.us.shared_finances.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository userRepository;
    private final WalletPermissionService permissionService;

    // POST /api/wallets/{walletId}/invitations
    @Transactional
    public InvitationResponse invite(Long walletId, Long userId, InvitationRequest request) {
        Membership ownerMembership = permissionService.requireOwner(walletId, userId);

        if (request.role() == WalletRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_ROLE");
        }

        String email = request.email().trim().toLowerCase();
        User invitee = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NO_ACCOUNT"));

        if (invitee.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SELF");
        }

        if (membershipRepository.existsByWalletIdAndUserId(walletId, invitee.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ALREADY_MEMBER");
        }

        if (invitationRepository.existsByWalletIdAndInviteeIdAndStatus(walletId, invitee.getId(), InvitationStatus.PENDING)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ALREADY_INVITED");
        }

        Invitation invitation = new Invitation();
        invitation.setWallet(ownerMembership.getWallet());
        invitation.setInvitee(invitee);
        invitation.setRole(request.role());
        invitation.setStatus(InvitationStatus.PENDING);
        invitationRepository.save(invitation);

        return toResponse(invitation);
    }

    // GET /api/invitations
    @Transactional(readOnly = true)
    public List<InvitationResponse> getMyInvitations(Long userId) {
        List<InvitationResponse> result = new ArrayList<>();

        for (Invitation invitation : invitationRepository.findByInviteeIdAndStatus(userId, InvitationStatus.PENDING)) {
            result.add(toResponse(invitation));
        }
        return result;
    }

    // POST /api/invitations/{invitationId}/accept
    @Transactional
    public void accept(Long invitationId, Long userId) {
        Invitation invitation = findMyPendingInvitation(invitationId, userId);

        if (membershipRepository.existsByWalletIdAndUserId(invitation.getWallet().getId(), userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ALREADY_MEMBER");
        }

        Membership membership = new Membership();
        membership.setWallet(invitation.getWallet());
        membership.setUser(invitation.getInvitee());
        membership.setRole(invitation.getRole());
        membershipRepository.save(membership);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    // POST /api/invitations/{invitationId}/reject
    @Transactional
    public void reject(Long invitationId, Long userId) {
        Invitation invitation = findMyPendingInvitation(invitationId, userId);

        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);
    }

    private Invitation findMyPendingInvitation(Long invitationId, Long userId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "INVITATION_NOT_FOUND"));

        if (!invitation.getInvitee().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "INVITATION_NOT_FOUND");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "INVITATION_ALREADY_ANSWERED");
        }
        return invitation;
    }

    private InvitationResponse toResponse(Invitation invitation) {
        Wallet wallet = invitation.getWallet();

        Optional<Membership> ownerMembership = membershipRepository.findByWalletIdAndRole(wallet.getId(), WalletRole.OWNER);
        Membership membership = ownerMembership.orElseThrow();
        User owner = membership.getUser();

        return new InvitationResponse(
                invitation.getId(),
                wallet.getId(),
                wallet.getName(),
                owner.getId(),
                owner.getDisplayName(),
                invitation.getInvitee().getEmail(),
                invitation.getRole(),
                invitation.getCreatedAt()
        );
    }
}