package msig.test.candidate.dev.budhioct.repository;

import msig.test.candidate.dev.budhioct.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {

    Optional<Users> findFirstByToken(String token);

    Optional<Users> findFirstByUsername(String username);
}
