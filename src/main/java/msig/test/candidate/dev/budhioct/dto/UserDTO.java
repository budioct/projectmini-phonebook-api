package msig.test.candidate.dev.budhioct.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import msig.test.candidate.dev.budhioct.model.Users;

import java.time.LocalDateTime;

public class UserDTO {

    @Setter
    @Getter
    @Builder
    public static class RegisterUserRequest {
        @NotBlank
        @Size(max = 100)
        private String username;
        @NotBlank
        @Size(max = 100)
        private String password;
        @NotBlank
        @Size(max = 100)
        private String name;
    }

    @Setter
    @Getter
    @Builder
    public static class UpdateUserRequest{
        @NotBlank
        @Size(max = 100)
        private String name;
        @Size(max = 100)
        private String password;
    }

    @Setter
    @Getter
    @Builder
    public static class LoginUserRequest {
        @NotBlank
        @Size(max = 100)
        private String username;
        @NotBlank
        @Size(max = 100)
        private String password;
    }

    @Setter
    @Getter
    @Builder
    public static class TokenResponse {
        private String token;
        private Long expiredAt;
    }

    @Setter
    @Getter
    @Builder
    public static class UserResponse {
        private String username;
        private String name;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    public static TokenResponse tokenResponse(Users users) {
        return TokenResponse.builder()
                .token(users.getToken())
                .expiredAt(users.getTokenExpiredAt())
                .build();
    }

    public static UserResponse toUserResponse(Users users) {
        return UserResponse.builder()
                .username(users.getUsername())
                .name(users.getName())
                .createdAt(users.getCreatedAt())
                .updatedAt(users.getUpdatedAt())
                .build();
    }

}
