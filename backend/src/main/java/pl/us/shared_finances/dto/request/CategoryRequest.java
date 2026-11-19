package pl.us.shared_finances.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pl.us.shared_finances.entity.enums.CategoryType;

public record CategoryRequest(
        @NotBlank(message = "Nazwa kategorii jest wymagana")
        @Size(max = 50, message = "Nazwa może mieć maksymalnie 50 znaków")
        String name,

        @NotNull(message = "Typ kategorii jest wymagany")
        CategoryType type
) {
}
