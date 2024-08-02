package msig.test.candidate.dev.budhioct.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import msig.test.candidate.dev.budhioct.controller.handler.RestResponse;
import msig.test.candidate.dev.budhioct.dto.AddressDTO;
import msig.test.candidate.dev.budhioct.dto.ContactDTO;
import msig.test.candidate.dev.budhioct.model.Addresses;
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
public class AddressControllerTest {

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

        // sebelum di test semua record data table akan di hapus
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

        Contacts contacts = new Contacts();
        contacts.setFirstName("hendi");
        contacts.setLastName("wicaksono");
        contacts.setEmail("hendi@test.com");
        contacts.setPhone("08143132345");
        contacts.setUser(users);
        contactRepository.save(contacts);

    }

    @Test
    void testCreateAddressBadRequest() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        AddressDTO.CreateAddressRequest request = AddressDTO.CreateAddressRequest.builder()
                .country("")
                .build();

        mockMvc.perform(
                post("/api/contacts/" + contacts.getId() + "/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response);
            Assertions.assertEquals(400, response.getStatus_code());

        });
    }

    @Test
    void testCreateAddressUnauthorized() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        mockMvc.perform(
                post("/api/contacts/" + contacts.getId() + "/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                //.header("X-API-TOKEN", "test")
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
    void testCreateAddressSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        AddressDTO.CreateAddressRequest request = AddressDTO.CreateAddressRequest.builder()
                .street("jl. cendrawasi")
                .city("jakarta")
                .province("jawa barat")
                .country("indonesia")
                .postalCode("15157")
                .build();

        mockMvc.perform(
                post("/api/contacts/" + contacts.getId() + "/address")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            RestResponse.object<AddressDTO.AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertEquals(request.getStreet(), response.getData().getStreet());
            Assertions.assertEquals(request.getCity(), response.getData().getCity());
            Assertions.assertEquals(request.getProvince(), response.getData().getProvince());
            Assertions.assertEquals(request.getCountry(), response.getData().getCountry());
            Assertions.assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            Assertions.assertTrue(addressRepository.existsById(response.getData().getId()));

        });
    }

    @Test
    void testGetDetailAddressNotFound() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        mockMvc.perform(
                get("/api/contacts/" + contacts.getId() + "/address/" + 9999999)
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
    void testGetDetailAddressUnauthorized() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        mockMvc.perform(
                get("/api/contacts/" + contacts.getId() + "/address/" + 9999999)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                //.header("X-API-TOKEN", "test")
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
    void testGetDetailAddressSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        Addresses address = new Addresses();
        address.setContact(contacts);
        address.setStreet("jl. cendrawasi");
        address.setCity("jakarta");
        address.setProvince("jawa barat");
        address.setCountry("indonesia");
        address.setPostalCode("15157");
        addressRepository.save(address);

        mockMvc.perform(
                get("/api/contacts/" + contacts.getId() + "/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<AddressDTO.AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertEquals(address.getStreet(), response.getData().getStreet());
            Assertions.assertEquals(address.getCity(), response.getData().getCity());
            Assertions.assertEquals(address.getProvince(), response.getData().getProvince());
            Assertions.assertEquals(address.getCountry(), response.getData().getCountry());
            Assertions.assertEquals(address.getPostalCode(), response.getData().getPostalCode());

            Assertions.assertTrue(addressRepository.existsById(response.getData().getId()));

        });
    }

    @Test
    void testUpdateAddressBadRequest() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        Addresses address = new Addresses();
        address.setContact(contacts);
        address.setStreet("jl. cendrawasi");
        address.setCity("jakarta");
        address.setProvince("jawa barat");
        address.setCountry("indonesia");
        address.setPostalCode("15157");
        addressRepository.save(address);

        AddressDTO.CreateAddressRequest request = AddressDTO.CreateAddressRequest.builder()
                .country("")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + contacts.getId() + "/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            RestResponse.restError<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertNotNull(response);
            Assertions.assertEquals(400, response.getStatus_code());

        });
    }

    @Test
    void testUpdateAddressUnauthorized() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        mockMvc.perform(
                put("/api/contacts/" + contacts.getId() + "/address/" + 9999)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testUpdateAddressSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        Addresses address = new Addresses();
        address.setContact(contacts);
        address.setStreet("jl. cendrawasi");
        address.setCity("jakarta");
        address.setProvince("jawa barat");
        address.setCountry("indonesia");
        address.setPostalCode("15157");
        addressRepository.save(address);

        AddressDTO.CreateAddressRequest request = AddressDTO.CreateAddressRequest.builder()
                .street("jl. cendrawasi update")
                .city("jakarta update")
                .province("jawa barat update")
                .country("indonesia")
                .postalCode("15157")
                .build();

        mockMvc.perform(
                put("/api/contacts/" + contacts.getId() + "/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<AddressDTO.AddressResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertEquals(request.getStreet(), response.getData().getStreet());
            Assertions.assertEquals(request.getCity(), response.getData().getCity());
            Assertions.assertEquals(request.getProvince(), response.getData().getProvince());
            Assertions.assertEquals(request.getCountry(), response.getData().getCountry());
            Assertions.assertEquals(request.getPostalCode(), response.getData().getPostalCode());

            Assertions.assertTrue(addressRepository.existsById(response.getData().getId()));

        });

    }

    @Test
    void testDeleteAddressUnauthorized() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        mockMvc.perform(
                delete("/api/contacts/" + contacts.getId() + "/address/" + 9999)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testDeleteAddressIdNotIsFound() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        mockMvc.perform(
                delete("/api/contacts/" + contacts.getId() + "/address/" + 99999)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void testDeleteAddressSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        Addresses address = new Addresses();
        address.setContact(contacts);
        address.setStreet("jl. cendrawasi");
        address.setCity("jakarta");
        address.setProvince("jawa barat");
        address.setCountry("indonesia");
        address.setPostalCode("15157");
        addressRepository.save(address);

        mockMvc.perform(
                delete("/api/contacts/" + contacts.getId() + "/address/" + address.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            RestResponse.object<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            Assertions.assertEquals(200, response.getStatus_code());

            Assertions.assertFalse(addressRepository.existsById(address.getId()));

        });

    }

    @Test
    void listAddressUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
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
    void listAddressSuccess() throws Exception {

        Users users = userRepository.findFirstByUsername("budioct").orElse(null);
        Contacts contacts = contactRepository.findFirstByUser(users).orElse(null);

        for (int i = 0; i < 5; i++) {
            Addresses address = new Addresses();
            address.setContact(contacts);
            address.setStreet("jl. cendrawasi");
            address.setCity("jakarta");
            address.setProvince("jawa barat");
            address.setCountry("indonesia");
            address.setPostalCode("15157");
            addressRepository.save(address);
        }

        mockMvc.perform(
                get("/api/addresses")
                        .queryParam("street", "jl. cendrawasi")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<AddressDTO.AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(5, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });

        mockMvc.perform(
                get("/api/addresses")
                        .queryParam("city", "jakarta")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<AddressDTO.AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(5, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });

        mockMvc.perform(
                get("/api/addresses")
                        .queryParam("province", "jawa barat")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<AddressDTO.AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(5, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });

        mockMvc.perform(
                get("/api/addresses")
                        .queryParam("country", "indonesia")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<AddressDTO.AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(5, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });

        mockMvc.perform(
                get("/api/addresses")
                        .queryParam("postalCode", "15157")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-API-TOKEN", "test")
        ).andExpectAll(
                status().isOk()
        ).andDo(new ResultHandler() {
            @Override
            public void handle(MvcResult result) throws Exception {
                RestResponse.list<List<AddressDTO.AddressResponse>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                });

                Assertions.assertNotNull(response.getData());

                Assertions.assertEquals(5, response.getData().size());
                Assertions.assertEquals(0, response.getPaging().getCurrentPage());
                Assertions.assertEquals(1, response.getPaging().getTotalPage());
                Assertions.assertEquals(10, response.getPaging().getSizePage());

            }
        });

    }

}
