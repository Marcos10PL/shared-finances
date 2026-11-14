package pl.us.shared_finances.dto.response;

public record UserResponse(
        Long id,
        String displayName,
        String email) {
}
