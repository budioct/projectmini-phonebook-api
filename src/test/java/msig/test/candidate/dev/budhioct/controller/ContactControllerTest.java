package msig.test.candidate.dev.budhioct.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import msig.test.candidate.dev.budhioct.controller.handler.RestResponse;
import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.model.Users;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        contactRepository.deleteAll();
        userRepository.deleteAll();

        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken("test");
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);
    }

    @Test
    void testCreateContractBlank() throws Exception {
        ContactDTO.CreateContactRequest request = ContactDTO.CreateContactRequest.builder()
                .firstName("")
                .lastName("")
                .email("")
                .phone("")
                .build();

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response);
                Assertions.assertEquals(400, response.getStatus_code());

            }
        });
    }

    @Test
    void testCreateContractUnauthorized() throws Exception {
        ContactDTO.CreateContactRequest request = ContactDTO.CreateContactRequest.builder()
                .firstName("")
                .lastName("")
                .email("")
                .phone("")
                .build();

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
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
    void testCreateContractSuccess() throws Exception {
        ContactDTO.CreateContactRequest request = ContactDTO.CreateContactRequest.builder()
                .firstName("hendi")
                .lastName("wicaksono")
                .email("hendi@test.com")
                .phone("08143132345")
                .build();

        mockMvc.perform(
                post("/api/contacts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isCreated()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.object<ContactDTO.ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(request.getFirstName(), response.getData().getFirstName());
                Assertions.assertEquals(request.getLastName(), response.getData().getLastName());
                Assertions.assertEquals(request.getEmail(), response.getData().getEmail());
                Assertions.assertEquals(request.getPhone(), response.getData().getPhone());

                assertTrue(contactRepository.existsById(response.getData().getId()));

            }
        });
    }

}
