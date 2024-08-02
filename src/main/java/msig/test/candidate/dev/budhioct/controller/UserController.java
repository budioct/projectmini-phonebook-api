package msig.test.candidate.dev.budhioct.controller;

import lombok.RequiredArgsConstructor;
import msig.test.candidate.dev.budhioct.controller.handler.RestResponse;
import msig.test.candidate.dev.budhioct.dto.UserDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.service.UserService;
import msig.test.candidate.dev.budhioct.utilities.Constants;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping(
            path = "/api/users",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RestResponse.object<String>> register(@RequestBody UserDTO.RegisterUserRequest request) {
        service.register(request);
        RestResponse.object<String> response = RestResponse.object.<String>builder()
                .status_code(Constants.CREATED)
                .message(Constants.CREATE_MESSAGE)
                .data("")
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<UserDTO.TokenResponse> login(@RequestBody UserDTO.LoginUserRequest request) {
        UserDTO.TokenResponse tokenResponse = service.login(request);
        return RestResponse.object.<UserDTO.TokenResponse>builder()
                .status_code(Constants.OK)
                .message(Constants.AUTH_LOGIN_MESSAGE)
                .data(tokenResponse)
                .build();
    }

    @DeleteMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<String> logout(Users users) {
        service.logout(users);
        return RestResponse.object.<String>builder()
                .status_code(Constants.OK)
                .message(Constants.AUTH_LOGOUT_MESSAGE)
                .data("")
                .build();
    }

    @GetMapping(
            path = "/api/users/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<UserDTO.UserResponse> getDetailUser(Users users) {
        UserDTO.UserResponse userResponse = service.getDetailUser(users);
        return RestResponse.object.<UserDTO.UserResponse>builder()
                .status_code(Constants.OK)
                .message(Constants.ITEM_EXIST_MESSAGE)
                .data(userResponse)
                .build();
    }

    @GetMapping(
            path = "/api/users",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.list<List<UserDTO.UserResponse>> getListUsers(Users users,
                                                                      @RequestParam Map<String, Object> filter) {
        Page<UserDTO.UserResponse> userResponses = service.getListUsers(users, filter);
        return RestResponse.list.<List<UserDTO.UserResponse>>builder()
                .status_code(Constants.OK)
                .message(Constants.ITEM_EXIST_MESSAGE)
                .count_data(userResponses.getContent().size())
                .data(userResponses.getContent())
                .paging(RestResponse.restPagingResponse.builder()
                        .currentPage(userResponses.getNumber())
                        .totalPage(userResponses.getTotalPages())
                        .sizePage(userResponses.getSize())
                        .build())
                .build();
    }

    @PatchMapping(
            path = "/api/users/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public RestResponse.object<UserDTO.UserResponse> updateUser(Users users, @RequestBody UserDTO.UpdateUserRequest request) {
        UserDTO.UserResponse userResponse = service.update(users, request);
        return RestResponse.object.<UserDTO.UserResponse>builder()
                .status_code(Constants.OK)
                .message(Constants.UPDATE_MESSAGE)
                .data(userResponse)
                .build();
    }

}
