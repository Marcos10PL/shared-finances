package pl.us.shared_finances.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletRequest(
        @NotBlank(message = "Nazwa portfela jest wymagana")
        @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków")
        String name
) {
}