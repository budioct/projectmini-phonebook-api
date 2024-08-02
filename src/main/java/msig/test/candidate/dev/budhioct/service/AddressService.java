package msig.test.candidate.dev.budhioct.service;

import msig.test.candidate.dev.budhioct.dto.AddressDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface AddressService {

    AddressDTO.AddressResponse create(Users users, AddressDTO.CreateAddressRequest request);
    Page<AddressDTO.AddressResponse> getListAddress(Users users, Map<String, Object> filter);
    AddressDTO.AddressResponse getDetail(Users users, AddressDTO.DetailAddressRequest request);
    AddressDTO.AddressResponse update(Users users, AddressDTO.UpdateAddressRequest request);
    void delete(Users users, AddressDTO.DetailAddressRequest request);
}
