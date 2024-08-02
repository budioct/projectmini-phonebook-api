package msig.test.candidate.dev.budhioct.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import msig.test.candidate.dev.budhioct.controller.handler.RestResponse;
import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.model.Contacts;
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
public class ContactControllerTest {

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

    @Test
    void testGetContactNotFound() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        mockMvc.perform(
                get("/api/contacts/" + contacts.getId() + 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response);
            Assertions.assertEquals(404, response.getStatus_code());

        });
    }

    @Test
    void testGetContactUnauthorized() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        mockMvc.perform(
                get("/api/contacts/" + contacts.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "salah")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response);
            Assertions.assertEquals(401, response.getStatus_code());

        });
    }

    @Test
    void testGetContactSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        mockMvc.perform(
                get("/api/contacts/" + contacts.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<ContactDTO.ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getData());

            Assertions.assertEquals(contacts.getFirstName(), response.getData().getFirstName());
            Assertions.assertEquals(contacts.getLastName(), response.getData().getLastName());
            Assertions.assertEquals(contacts.getEmail(), response.getData().getEmail());
            Assertions.assertEquals(contacts.getPhone(), response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));

        });
    }

    @Test
    void testUpdateContractBlank() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        ContactDTO.UpdateContactRequest request = ContactDTO.UpdateContactRequest.builder()
                .id(contacts.getId())
                .firstName("")
                .lastName("")
                .email("")
                .phone("")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + request.getId())
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
    void testUpdateContractUnauthorized() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        ContactDTO.UpdateContactRequest request = ContactDTO.UpdateContactRequest.builder()
                .id(contacts.getId())
                .firstName("")
                .lastName("")
                .email("")
                .phone("")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + request.getId())
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
    void testUpdateContactSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        ContactDTO.UpdateContactRequest request = ContactDTO.UpdateContactRequest.builder()
                .id(contacts.getId())
                .firstName("hendi update")
                .lastName("wicaksono update")
                .email("hendi@test.com")
                .phone("08143132345")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + request.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<ContactDTO.ContactResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getData());

            Assertions.assertEquals(request.getFirstName(), response.getData().getFirstName());
            Assertions.assertEquals(request.getLastName(), response.getData().getLastName());
            Assertions.assertEquals(request.getEmail(), response.getData().getEmail());
            Assertions.assertEquals(request.getPhone(), response.getData().getPhone());

            assertTrue(contactRepository.existsById(response.getData().getId()));

        });
    }

    @Test
    void testDeleteContractBlank() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        mockMvc.perform(
                delete("/api/contacts/" + contacts.getId() + 1)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isNotFound()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response);
                Assertions.assertEquals(404, response.getStatus_code());

            }
        });
    }

    @Test
    void testDeleteContractUnauthorized() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        mockMvc.perform(
                delete("/api/contacts/" + contacts.getId())
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
    void testDeleteContactSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        mockMvc.perform(
                delete("/api/contacts/" + contacts.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response.getData());
            Assertions.assertEquals(200, response.getStatus_code());

        });
    }

    @Test
    void testGetListContactUnauthorized() throws Exception {

        mockMvc.perform(
                get("/api/contacts")
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
    void testGetListContactNotFound() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

        mockMvc.perform(
                get("/api/contacts")
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
    void testGetListContactSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);

        for (int i = 0; i < 10; i++) {
            Contacts contacts = new Contacts();
            contacts.setFirstName("hendi");
            contacts.setLastName("wicaksono");
            contacts.setEmail("hendi@test.com");
            contacts.setPhone("08143132345");
            contacts.setUser(users);
            contactRepository.save(contacts);
        }

        mockMvc.perform(
                get("/api/contacts")
                        .queryParam("firstName", "hendi")
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
                get("/api/contacts")
                        .queryParam("lastName", "wicaksono")
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
                get("/api/contacts")
                        .queryParam("email", "hendi@test.com")
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
                get("/api/contacts")
                        .queryParam("phone", "08143132345")
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
