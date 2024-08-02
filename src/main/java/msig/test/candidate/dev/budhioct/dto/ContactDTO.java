package msig.test.candidate.dev.budhioct.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import msig.test.candidate.dev.budhioct.model.Contacts;

public class ContactDTO {

    @Setter
    @Getter
    @Builder
    public static class ContactResponse {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
    }

    @Setter
    @Getter
    @Builder
    public static class CreateContactRequest {
        @NotBlank
        @Size(max = 100)
        private String firstName;
        @Size(max = 100)
        private String lastName;
        @Size(max = 100)
        @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
        private String email;
        @NotBlank
        @Size(max = 12)
        private String phone;
    }

    @Setter
    @Getter
    @Builder
    public static class UpdateContactRequest {
        @JsonIgnore
        private Long id;
        @NotBlank
        @Size(max = 100)
        private String firstName;
        @Size(max = 100)
        private String lastName;
        @Size(max = 100)
        @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE)
        private String email;
        @Size(max = 100)
        private String phone;
    }

    public static ContactResponse toContactResponse(Contacts contacts) {
        return ContactResponse.builder()
                .id(contacts.getId())
                .firstName(contacts.getFirstName())
                .lastName(contacts.getLastName())
                .email(contacts.getEmail())
                .phone(contacts.getPhone())
                .build();
    }

}
