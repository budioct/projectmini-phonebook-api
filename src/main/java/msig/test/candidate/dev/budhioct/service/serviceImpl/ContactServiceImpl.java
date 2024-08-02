package msig.test.candidate.dev.budhioct.service.serviceImpl;

import msig.test.candidate.dev.budhioct.common.Models;
import msig.test.candidate.dev.budhioct.common.ValidationService;
import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.model.Contacts;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.repository.ContactRepository;
import msig.test.candidate.dev.budhioct.service.ContactService;
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
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ValidationService validation;

    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public ContactDTO.ContactResponse create(Users users, ContactDTO.CreateContactRequest request) {
        validation.validate(request);

        Contacts contacts = new Contacts();
        contacts.setFirstName(request.getFirstName());
        contacts.setLastName(request.getLastName());
        contacts.setEmail(request.getEmail());
        contacts.setPhone(request.getPhone());
        contacts.setUser(users);

        contactRepository.save(contacts);
        return ContactDTO.toContactResponse(contacts);
    }

    @Transactional(readOnly = true)
    public Page<ContactDTO.ContactResponse> getListContacts(Map<String, Object> filter) {
        Models<Contacts> models = new Models<>();
        Page<Contacts> contactsPage = contactRepository.findAll(models.where(filter), models.pageableSort(filter));
        List<ContactDTO.ContactResponse> contactResponse = contactsPage.getContent().stream().map(ContactDTO::toContactResponse).collect(Collectors.toList());

        if (contactResponse.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "List Contacts not found");
        }

        return new PageImpl<>(contactResponse, contactsPage.getPageable(), contactsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ContactDTO.ContactResponse getDetail(Users users, Long id) {
        Contacts contacts = contactRepository.findFirstByUserAndId(users, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        return ContactDTO.toContactResponse(contacts);
    }

    @Transactional
    public ContactDTO.ContactResponse update(Users users, ContactDTO.UpdateContactRequest request) {
        validation.validate(request);

        Contacts contact = contactRepository.findFirstByUserAndId(users, request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        contactRepository.save(contact);

        return ContactDTO.toContactResponse(contact);
    }

    @Transactional
    public void delete(Users users, Long id) {
        Contacts contact = contactRepository.findFirstByUserAndId(users, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));

        contactRepository.delete(contact);
    }
}
