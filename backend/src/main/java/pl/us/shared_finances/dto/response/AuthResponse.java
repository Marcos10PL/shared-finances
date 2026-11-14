package pl.us.shared_finances.dto.response;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
