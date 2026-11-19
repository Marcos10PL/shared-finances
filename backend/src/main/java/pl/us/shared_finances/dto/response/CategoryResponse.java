package pl.us.shared_finances.dto.response;

import pl.us.shared_finances.entity.enums.CategoryType;

public record CategoryResponse(
        Long id,
        String name,
        CategoryType type,
        Long userId // null = kategoria domyślna, nie można jej edytować ani usunąć
) {
}
