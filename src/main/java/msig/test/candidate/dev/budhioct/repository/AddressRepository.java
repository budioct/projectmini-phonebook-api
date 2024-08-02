package msig.test.candidate.dev.budhioct.repository;

import msig.test.candidate.dev.budhioct.model.Addresses;
import msig.test.candidate.dev.budhioct.model.Contacts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Addresses, Long>, JpaSpecificationExecutor<Addresses> {

    Optional<Addresses> findFirstByContactAndId(Contacts contact, Long id);

    List<Addresses> findAllByContact(Contacts contact);

}
