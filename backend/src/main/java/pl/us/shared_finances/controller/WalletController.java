package pl.us.shared_finances.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.us.shared_finances.dto.request.WalletRequest;
import pl.us.shared_finances.dto.response.WalletResponse;
import pl.us.shared_finances.service.WalletService;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public List<WalletResponse> getMyWallets(@AuthenticationPrincipal Long userId) {
        return walletService.getMyWallets(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@AuthenticationPrincipal Long userId,
                                       @Valid @RequestBody WalletRequest request) {
        return walletService.create(userId, request);
    }

    @GetMapping("/{walletId}")
    public WalletResponse getWallet(@PathVariable Long walletId,
                                    @AuthenticationPrincipal Long userId) {
        return walletService.getWallet(walletId, userId);
    }

    @PutMapping("/{walletId}")
    public WalletResponse renameWallet(@PathVariable Long walletId,
                                       @AuthenticationPrincipal Long userId,
                                       @Valid @RequestBody WalletRequest request) {
        return walletService.renameWallet(walletId, userId, request);
    }

    @DeleteMapping("/{walletId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWallet(@PathVariable Long walletId,
                             @AuthenticationPrincipal Long userId) {
        walletService.deleteWallet(walletId, userId);
    }
}