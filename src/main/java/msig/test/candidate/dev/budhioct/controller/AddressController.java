package msig.test.candidate.dev.budhioct.controller;

import msig.test.candidate.dev.budhioct.controller.handler.RestResponse;
import msig.test.candidate.dev.budhioct.dto.AddressDTO;
import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.service.AddressService;
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
public class AddressController {

    @Autowired
    private AddressService service;

    @PostMapping(
            path = "/api/contacts/{contactId}/address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RestResponse.object<AddressDTO.AddressResponse>> create(Users users,
                                                                                  @RequestBody AddressDTO.CreateAddressRequest request,
                                                                                  @PathVariable("contactId") Long id) {
        request.setContactId(id);
        AddressDTO.AddressResponse addressResponse = service.create(users, request);
        RestResponse.object<AddressDTO.AddressResponse> response = RestResponse.object.<AddressDTO.AddressResponse>builder()
                .status_code(Constants.CREATED)
                .message(Constants.CREATE_MESSAGE)
                .data(addressResponse)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping(
            path = "/api/addresses",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.list<List<AddressDTO.AddressResponse>> getList(Users users,
                                                                       @RequestParam Map<String, Object> filter) {
        Page<AddressDTO.AddressResponse> addressResponses = service.getListAddress(users, filter);
        return RestResponse.list.<List<AddressDTO.AddressResponse>>builder()
                .status_code(Constants.OK)
                .message(Constants.ITEM_EXIST_MESSAGE)
                .count_data(addressResponses.getContent().size())
                .data(addressResponses.getContent())
                .paging(RestResponse.restPagingResponse.builder()
                        .currentPage(addressResponses.getNumber())
                        .totalPage(addressResponses.getTotalPages())
                        .sizePage(addressResponses.getSize())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/api/contacts/{contactId}/address/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<AddressDTO.AddressResponse> getDetail(Users users,
                                                                     @PathVariable(name = "contactId") Long contactId,
                                                                     @PathVariable(name = "addressId") Long addressId,
                                                                     AddressDTO.DetailAddressRequest request) {
        request.setAddressId(addressId);
        request.setContactId(contactId);
        AddressDTO.AddressResponse addressResponse = service.getDetail(users, request);
        return RestResponse.object.<AddressDTO.AddressResponse>builder()
                .status_code(Constants.OK)
                .message(Constants.ITEM_EXIST_MESSAGE)
                .data(addressResponse)
                .build();
    }

    @PutMapping(
            path = "/api/contacts/{contactId}/address/{addressId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<AddressDTO.AddressResponse> update(Users users,
                                                                  @PathVariable(name = "contactId") Long contactId,
                                                                  @PathVariable(name = "addressId") Long addressId,
                                                                  @RequestBody AddressDTO.UpdateAddressRequest request) {
        request.setAddressId(addressId);
        request.setContactId(contactId);
        AddressDTO.AddressResponse addressResponse = service.update(users, request);
        return RestResponse.object.<AddressDTO.AddressResponse>builder()
                .status_code(Constants.OK)
                .message(Constants.UPDATE_MESSAGE)
                .data(addressResponse)
                .build();
    }

    @DeleteMapping(
            path = "/api/contacts/{contactId}/address/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<String> delete(Users users,
                                              @PathVariable(name = "contactId") Long contactId,
                                              @PathVariable(name = "addressId") Long addressId,
                                              AddressDTO.DetailAddressRequest request) {
        request.setAddressId(addressId);
        request.setContactId(contactId);
        service.delete(users, request);
        return RestResponse.object.<String>builder()
                .status_code(Constants.OK)
                .message(Constants.DELETE_MESSAGE)
                .data("")
                .build();
    }


}
