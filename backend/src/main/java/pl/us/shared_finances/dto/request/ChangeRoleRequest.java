package pl.us.shared_finances.dto.request;

import jakarta.validation.constraints.NotNull;
import pl.us.shared_finances.entity.enums.WalletRole;

public record ChangeRoleRequest(
        @NotNull(message = "Rola jest wymagana")
        WalletRole role
) {
}
