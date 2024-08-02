package msig.test.candidate.dev.budhioct.service;

import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ContactService {

    ContactDTO.ContactResponse create(Users users, ContactDTO.CreateContactRequest request);
    Page<ContactDTO.ContactResponse> getListContacts(Map<String, Object> filter);
    ContactDTO.ContactResponse getDetail(Users users, Long id);
    ContactDTO.ContactResponse update(Users users, ContactDTO.UpdateContactRequest request);
    void delete(Users users, Long id);
}
