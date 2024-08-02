package msig.test.candidate.dev.budhioct.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import msig.test.candidate.dev.budhioct.controller.handler.RestResponse;
import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.dto.UserDTO;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.repository.AddressRepository;
import msig.test.candidate.dev.budhioct.repository.ContactRepository;
import msig.test.candidate.dev.budhioct.repository.UserRepository;
import msig.test.candidate.dev.budhioct.utilities.BCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        contactRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {
        UserDTO.RegisterUserRequest request = UserDTO.RegisterUserRequest.builder()
                .username("budioct")
                .password("rahasia")
                .name("budhi octaviansyah")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isCreated()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.object<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.object<String>>() {
                });

                Assertions.assertNotNull(response);
                Assertions.assertEquals(201, response.getStatus_code());
            }
        });
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        UserDTO.RegisterUserRequest request = UserDTO.RegisterUserRequest.builder()
                .username("")
                .password("")
                .name("")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());
                Assertions.assertEquals(400, response.getStatus_code());
            }
        });
    }

    @Test
    void testRegisterDuplicate() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        userRepository.save(users);

        UserDTO.RegisterUserRequest request = UserDTO.RegisterUserRequest.builder()
                .username("budioct")
                .password("rahasia")
                .name("budhi octaviansyah")
                .build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(
                status().isBadRequest()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());
                Assertions.assertEquals(400, response.getStatus_code());
            }
        });
    }

    @Test
    void getDetailUserSuccess() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<UserDTO.UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.object<UserDTO.UserResponse>>() {
            });

            assertEquals(200, response.getStatus_code());
            assertEquals("budioct", response.getData().getUsername());
            assertEquals("budhi octaviansyah", response.getData().getName());
        });
    }

    @Test
    void getDetailUserUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals(401, response.getStatus_code());

        });
    }

    @Test
    void getDetailUserUnauthorizedTokenNotSend() throws Exception {
        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals(401, response.getStatus_code());
        });
    }

    @Test
    void getUserTokenExpired() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() - 10000000000L);
        userRepository.save(users);

        mockMvc.perform(
                get("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals(401, response.getStatus_code());
        });
    }

    @Test
    void testUpdateUserNotBlank() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

        UserDTO.UpdateUserRequest request = UserDTO.UpdateUserRequest.builder()
                .name("")
                .password("")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals(400, response.getStatus_code());
        });
    }

    @Test
    void testUpdateUserFailed() throws Exception {
        UserDTO.UpdateUserRequest request = null;

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals(401, response.getStatus_code());
        });
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

        UserDTO.UpdateUserRequest request = UserDTO.UpdateUserRequest.builder()
                .name("budhi update")
                .password("rahasia update")
                .build();

        mockMvc.perform(
                patch("/api/users/current")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<UserDTO.UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.object<UserDTO.UserResponse>>() {
            });

            assertEquals(200, response.getStatus_code());
            assertEquals("budhi update", response.getData().getName());

            Users userResponse = userRepository.findFirstByUsername("budioct").orElse(null);
            Assertions.assertNotNull(userResponse);
            Assertions.assertTrue(BCrypt.checkpw("rahasia update", userResponse.getPassword()));
        });
    }

    // test Authentication

    @Test
    void loginFailedUserNotFound() throws Exception {

        UserDTO.LoginUserRequest request = UserDTO.LoginUserRequest.builder()
                .username("budioct")
                .password("rahasia")
                .build();

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());
                Assertions.assertEquals(401, response.getStatus_code());
            }
        });
    }

    @Test
    void loginFailedWrongPassword() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

        UserDTO.LoginUserRequest request = UserDTO.LoginUserRequest.builder()
                .username("budioct")
                .password("salah")
                .build();

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
                });

                Assertions.assertNotNull(response.getErrors());
                Assertions.assertEquals(401, response.getStatus_code());
            }
        });
    }

    @Test
    void loginSuccess() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

        UserDTO.LoginUserRequest request = UserDTO.LoginUserRequest.builder()
                .username("budioct")
                .password("rahasia")
                .build();

        mockMvc.perform(
                post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.object<UserDTO.TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.object<UserDTO.TokenResponse>>() {
                });

                Assertions.assertNotNull(response.getData().getToken());
                Assertions.assertNotNull(response.getData().getExpiredAt());
                assertEquals(200, response.getStatus_code());

                Users userResponse = userRepository.findFirstByUsername("budioct").orElse(null);
                Assertions.assertNotNull(userResponse);
                Assertions.assertTrue(BCrypt.checkpw("rahasia", userResponse.getPassword()));
                Assertions.assertEquals(userResponse.getToken(), response.getData().getToken());
                Assertions.assertEquals(userResponse.getTokenExpiredAt(), response.getData().getExpiredAt());

            }
        });
    }

    @Test
    void testLogoutFailed() throws Exception {
        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.restError<String>>() {
            });

            Assertions.assertNotNull(response.getErrors());
            Assertions.assertEquals(401, response.getStatus_code());
        });
    }

    @Test
    void logoutSuccess() throws Exception {
        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

        mockMvc.perform(
                delete("/api/auth/logout")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
                //.content(objectMapper.writeValueAsString(user))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<RestResponse.object<String>>() {
            });

            Assertions.assertEquals(200, response.getStatus_code());

            Users userResponse = userRepository.findFirstByUsername("budioct").orElse(null);
            Assertions.assertNotNull(userResponse);
            Assertions.assertNull(userResponse.getTokenExpiredAt());
            Assertions.assertNull(userResponse.getToken());
        });
    }

    @Test
    void testGetListUsersUnauthorized() throws Exception {

        mockMvc.perform(
                get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response);
                Assertions.assertEquals(401, response.getStatus_code());
            }
        });
    }

    @Test
    void testGetListUsersNotFound() throws Exception {

        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

        mockMvc.perform(
                get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<ContactDTO.ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(1, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });
    }

    @Test
    void testGetListUsersSuccess() throws Exception {

        for (int i = 0; i < 10; i++) {
            Users users = new Users();
            users.setUsername("budioct");
            users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
            users.setName("budhi octaviansyah");
            users.setToken("test");
            users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
            userRepository.save(users);
        }

        mockMvc.perform(
                get("/api/users")
                        .queryParam("username", "budioct")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<ContactDTO.ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(10, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });

        mockMvc.perform(
                get("/api/users")
                        .queryParam("name", "budhi octaviansyah")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<ContactDTO.ContactResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(10, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });
    }

}
