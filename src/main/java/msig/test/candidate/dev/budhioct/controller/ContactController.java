package msig.test.candidate.dev.budhioct.controller;

import msig.test.candidate.dev.budhioct.controller.handler.RestResponse;
import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.dto.UserDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.service.ContactService;
import msig.test.candidate.dev.budhioct.utilities.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ContactController {

    @Autowired
    private ContactService service;

    @PostMapping(
            path = "/api/contacts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RestResponse.object<ContactDTO.ContactResponse>> create(Users users,
                                                                                  @RequestBody ContactDTO.CreateContactRequest request) {
        ContactDTO.ContactResponse contactResponse = service.create(users, request);
        RestResponse.object<ContactDTO.ContactResponse> response = RestResponse.object.<ContactDTO.ContactResponse>builder()
                .status_code(Constants.CREATED)
                .message(Constants.ITEM_EXIST_MESSAGE)
                .data(contactResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(
            path = "/api/contacts",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.list<List<ContactDTO.ContactResponse>> getList(@RequestParam Map<String, Object> filter) {
        Page<ContactDTO.ContactResponse> contactResponses = service.getListContacts(filter);
        return RestResponse.list.<List<ContactDTO.ContactResponse>>builder()
                .status_code(Constants.OK)
                .message(Constants.ITEM_EXIST_MESSAGE)
                .count_data(contactResponses.getContent().size())
                .data(contactResponses.getContent())
                .paging(RestResponse.restPagingResponse.builder()
                        .currentPage(contactResponses.getNumber())
                        .totalPage(contactResponses.getTotalPages())
                        .sizePage(contactResponses.getSize())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<ContactDTO.ContactResponse> getDetail(Users users,
                                                                            @PathVariable("contactId") Long id) {
        ContactDTO.ContactResponse contactResponse = service.getDetail(users, id);
        return RestResponse.object.<ContactDTO.ContactResponse>builder()
                .status_code(Constants.OK)
                .message(Constants.ITEM_EXIST_MESSAGE)
                .data(contactResponse)
                .build();
    }

    @PutMapping(
            path = "/api/contacts/{contactId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<ContactDTO.ContactResponse> updateContact(Users users,
                                                                         @PathVariable("contactId") Long id,
                                                                         @RequestBody ContactDTO.UpdateContactRequest request) {
        request.setId(id);
        ContactDTO.ContactResponse contactResponse = service.update(users, request);
        return RestResponse.object.<ContactDTO.ContactResponse>builder()
                .status_code(Constants.OK)
                .message(Constants.UPDATE_MESSAGE)
                .data(contactResponse)
                .build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<String> deleteContact(Users users,
                                                     @PathVariable("contactId") Long id) {
        service.delete(users, id);
        return RestResponse.object.<String>builder()
                .status_code(Constants.OK)
                .message(Constants.DELETE_MESSAGE)
                .data("")
                .build();
    }

}
