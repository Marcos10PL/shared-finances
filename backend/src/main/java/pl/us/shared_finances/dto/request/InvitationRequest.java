package pl.us.shared_finances.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.us.shared_finances.entity.enums.WalletRole;

public record InvitationRequest(
        @NotBlank(message = "E-mail jest wymagany")
        @Email(message = "Niepoprawny format adresu e-mail")
        @Size(max = 254, message = "E-mail może mieć maksymalnie 254 znaki")
        String email,

        @NotNull(message = "Rola jest wymagana")
        WalletRole role
) {
}
