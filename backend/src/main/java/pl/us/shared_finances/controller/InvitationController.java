package pl.us.shared_finances.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.us.shared_finances.dto.request.InvitationRequest;
import pl.us.shared_finances.dto.response.InvitationResponse;
import pl.us.shared_finances.service.InvitationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/api/wallets/{walletId}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponse invite(@PathVariable Long walletId,
                                     @AuthenticationPrincipal Long userId,
                                     @Valid @RequestBody InvitationRequest request) {
        return invitationService.invite(walletId, userId, request);
    }

    @GetMapping("/api/invitations")
    public List<InvitationResponse> getMyInvitations(@AuthenticationPrincipal Long userId) {
        return invitationService.getMyInvitations(userId);
    }

    @PostMapping("/api/invitations/{invitationId}/accept")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void accept(@PathVariable Long invitationId,
                       @AuthenticationPrincipal Long userId) {
        invitationService.accept(invitationId, userId);
    }

    @PostMapping("/api/invitations/{invitationId}/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(@PathVariable Long invitationId,
                       @AuthenticationPrincipal Long userId) {
        invitationService.reject(invitationId, userId);
    }
}