package pl.us.shared_finances.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

// PUT /api/wallets/{walletId}: pełna podmiana edytowalnych pól portfela.
// monthlyLimit = null usuwa limit (wtedy warningThreshold też jest zerowany).
public record WalletUpdateRequest(
        @NotBlank(message = "Nazwa portfela jest wymagana")
        @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków")
        String name,

        @Positive(message = "Limit musi być większy od zera")
        @Digits(integer = 10, fraction = 2, message = "Limit może mieć maksymalnie 2 miejsca po przecinku")
        @DecimalMax(value = "9999999.99", message = "Maksymalna kwota limitu to 9 999 999,99 zł")
        BigDecimal monthlyLimit,

        @Min(value = 1, message = "Próg musi być między 1 a 100%")
        @Max(value = 100, message = "Próg musi być między 1 a 100%")
        Integer warningThreshold
) {
}
