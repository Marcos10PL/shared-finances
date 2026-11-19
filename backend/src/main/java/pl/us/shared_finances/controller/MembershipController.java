package pl.us.shared_finances.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.us.shared_finances.dto.request.ChangeRoleRequest;
import pl.us.shared_finances.dto.response.MemberResponse;
import pl.us.shared_finances.service.MembershipService;

@RestController
@RequestMapping("/api/wallets/{walletId}")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PutMapping("/members/{memberId}/role")
    public MemberResponse changeRole(@PathVariable Long walletId,
                                     @PathVariable Long memberId,
                                     @AuthenticationPrincipal Long userId,
                                     @Valid @RequestBody ChangeRoleRequest request) {
        return membershipService.changeRole(walletId, userId, memberId, request);
    }

    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(@PathVariable Long walletId,
                             @PathVariable Long memberId,
                             @AuthenticationPrincipal Long userId) {
        membershipService.removeMember(walletId, userId, memberId);
    }

    @PostMapping("/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveWallet(@PathVariable Long walletId,
                            @AuthenticationPrincipal Long userId) {
        membershipService.leaveWallet(walletId, userId);
    }
}