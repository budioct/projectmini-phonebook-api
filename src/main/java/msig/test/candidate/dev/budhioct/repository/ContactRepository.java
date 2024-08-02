package msig.test.candidate.dev.budhioct.repository;

import msig.test.candidate.dev.budhioct.model.Contacts;
import msig.test.candidate.dev.budhioct.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contacts, Long>, JpaSpecificationExecutor<Contacts> {

    Optional<Contacts> findFirstByUserAndId(Users user, Long id);

}
