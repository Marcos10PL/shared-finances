package pl.us.shared_finances.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// edytować można tylko nazwę, typ zostaje bez zmian
public record CategoryRenameRequest(
        @NotBlank(message = "Nazwa kategorii jest wymagana")
        @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków")
        String name
) {
}
