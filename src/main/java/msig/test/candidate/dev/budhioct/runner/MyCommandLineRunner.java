package msig.test.candidate.dev.budhioct.runner;

import com.github.javafaker.Faker;
import msig.test.candidate.dev.budhioct.model.Addresses;
import msig.test.candidate.dev.budhioct.model.Contacts;
import msig.test.candidate.dev.budhioct.model.Users;
import msig.test.candidate.dev.budhioct.repository.AddressRepository;
import msig.test.candidate.dev.budhioct.repository.ContactRepository;
import msig.test.candidate.dev.budhioct.repository.UserRepository;
import msig.test.candidate.dev.budhioct.utilities.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class MyCommandLineRunner implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    private final Faker faker = new Faker(new Locale("in-ID"));;

    @Override
    public void run(String... args) throws Exception {

        Users users = new Users();
        users.setUsername("budioct");
        users.setPassword(BCrypt.hashpw("rahasia", BCrypt.gensalt()));
        users.setName("budhi octaviansyah");
        users.setToken(UUID.randomUUID().toString());
        users.setTokenExpiredAt(System.currentTimeMillis() + 10000000000L);
        userRepository.save(users);

//        Contacts contacts = null;
//        for (int i = 0; i < 350; i++) {
//            contacts = new Contacts();
//            contacts.setFirstName(faker.name().firstName());
//            contacts.setLastName(faker.name().lastName());
//            contacts.setEmail(faker.internet().emailAddress());
//            contacts.setPhone(faker.phoneNumber().phoneNumber());
//            contacts.setUser(users);
//            contactRepository.save(contacts);
//        }
//
//        for (int i = 0; i < 5; i++) {
//            Addresses address = new Addresses();
//            address.setContact(contacts);
//            address.setStreet(faker.address().streetAddress());
//            address.setCity(faker.address().city());
//            address.setProvince(faker.address().cityName());
//            address.setCountry(faker.address().country());
//            address.setPostalCode(faker.address().zipCode());
//            addressRepository.save(address);
//        }

        // Buat daftar contacts dan simpan di database
        List<Contacts> contactList = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            Contacts contacts = new Contacts();
            contacts.setFirstName(faker.name().firstName());
            contacts.setLastName(faker.name().lastName());
            contacts.setEmail(faker.internet().emailAddress());
            String phone = faker.phoneNumber().phoneNumber();
            if (phone.length() > 15) {
                phone = phone.substring(0, 15);
            }
            contacts.setPhone(phone);
            contacts.setUser(users);
            contactRepository.save(contacts);
            contactList.add(contacts);
        }

        // Simpan addresses dengan memilih contacts secara acak dari daftar contactList
        for (int i = 0; i < 100; i++) {
            Addresses address = new Addresses();
            address.setContact(contactList.get(faker.random().nextInt(contactList.size())));
            address.setStreet(faker.address().streetAddress());
            address.setCity(faker.address().city());
            address.setProvince(faker.address().cityName());
            address.setCountry(faker.address().country());
            address.setPostalCode(faker.address().zipCode());
            addressRepository.save(address);
        }


    }
}
