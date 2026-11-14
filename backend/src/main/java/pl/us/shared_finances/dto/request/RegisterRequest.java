package pl.us.shared_finances.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Nazwa użytkownika jest wymagana")
        @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków")
        String displayName,

        @NotBlank(message = "E-mail jest wymagany")
        @Email(message = "Niepoprawny format e-maila")
        @Size(max = 254, message = "E-mail może mieć maksymalnie 254 znaki")
        String email,

        @NotBlank(message = "Hasło jest wymagane")
        @Size(min = 8, max = 72, message = "Hasło musi mieć od 8 do 72 znaków")
        String password
) {
}
