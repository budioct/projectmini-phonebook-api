package msig.test.candidate.dev.budhioct.service.serviceImpl;

import msig.test.candidate.dev.budhioct.common.Models;
import msig.test.candidate.dev.budhioct.common.ValidationService;
import msig.test.candidate.dev.budhioct.dto.AddressDTO;
import msig.test.candidate.dev.budhioct.model.Addresses;
import msig.test.candidate.dev.budhioct.model.Contacts;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.repository.AddressRepository;
import msig.test.candidate.dev.budhioct.repository.ContactRepository;
import msig.test.candidate.dev.budhioct.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private ValidationService validation;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Transactional
    public AddressDTO.AddressResponse create(Users users, AddressDTO.CreateAddressRequest request) {
        validation.validate(request);

        Contacts contacts = contactRepository.findFirstByUserAndId(users, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Addresses address = new Addresses();
        address.setContact(contacts);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address);

        return AddressDTO.toAddressResponse(address);
    }

    @Transactional(readOnly = true)
    public Page<AddressDTO.AddressResponse> getListAddress(Map<String, Object> filter) {
        Models<Addresses> models = new Models<>();
        Page<Addresses> addressesPage = addressRepository.findAll(models.where(filter), models.pageableSort(filter));
        List<AddressDTO.AddressResponse> addressResponse = addressesPage.getContent().stream().map(AddressDTO::toAddressResponse).collect(Collectors.toList());

        if (addressResponse.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "List Addresses not found");
        }
        return new PageImpl<>(addressResponse, addressesPage.getPageable(), addressesPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public AddressDTO.AddressResponse getDetail(Users users, AddressDTO.DetailAddressRequest request) {
        Contacts contact = contactRepository.findFirstByUserAndId(users, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Addresses address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        return AddressDTO.toAddressResponse(address);
    }

    @Transactional
    public AddressDTO.AddressResponse update(Users users, AddressDTO.UpdateAddressRequest request) {
        validation.validate(request);

        Contacts contact = contactRepository.findFirstByUserAndId(users, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Addresses address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setProvince(request.getProvince());
        address.setCountry(request.getCountry());
        address.setPostalCode(request.getPostalCode());
        addressRepository.save(address); // proses DB

        return AddressDTO.toAddressResponse(address);
    }

    @Transactional
    public void delete(Users users, AddressDTO.DetailAddressRequest request) {
        Contacts contact = contactRepository.findFirstByUserAndId(users, request.getContactId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact is not found"));

        Addresses address = addressRepository.findFirstByContactAndId(contact, request.getAddressId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address is not found"));

        addressRepository.delete(address);
    }
}
