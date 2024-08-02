package msig.test.candidate.dev.budhioct.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import msig.test.candidate.dev.budhioct.model.Addresses;

public class AddressDTO {

    @Setter
    @Getter
    @Builder
    public static class AddressResponse {
        private Long id;
        private String street;
        private String city;
        private String province;
        private String country;
        private String postalCode;
    }

    @Setter
    @Getter
    @Builder
    public static class CreateAddressRequest {
        @JsonIgnore
        private Long contactId;
        @Size(max = 200)
        private String street;
        @Size(max = 100)
        private String city;
        @Size(max = 100)
        private String province;
        @NotBlank
        @Size(max = 100)
        private String country;
        @Size(max = 5)
        private String postalCode;
    }

    @Setter
    @Getter
    @Builder
    public static class DetailAddressRequest {
        @JsonIgnore
        private Long contactId;
        @JsonIgnore
        private Long addressId;
    }

    @Setter
    @Getter
    @Builder
    public static class UpdateAddressRequest {
        @JsonIgnore
        private Long contactId;
        @JsonIgnore
        private Long addressId;
        @Size(max = 200)
        private String street;
        @Size(max = 100)
        private String city;
        @Size(max = 100)
        private String province;
        @NotBlank
        @Size(max = 100)
        private String country;
        @Size(max = 10)
        private String postalCode;
    }

    public static AddressResponse toAddressResponse(Addresses addresses) {
        return AddressResponse.builder()
                .id(addresses.getId())
                .street(addresses.getStreet())
                .city(addresses.getCity())
                .province(addresses.getProvince())
                .country(addresses.getCountry())
                .postalCode(addresses.getPostalCode())
                .build();
    }

}
