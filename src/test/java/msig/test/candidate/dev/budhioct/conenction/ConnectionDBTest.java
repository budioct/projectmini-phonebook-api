package msig.test.candidate.dev.budhioct.conenction;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConnectionDBTest {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Disabled
    @Test
    void testEntityManagerFactorySuccess() {

        EntityManager entityManager = null;
        EntityTransaction entityTransaction = null;

        try {
            entityManager = entityManagerFactory.createEntityManager();
            entityTransaction = entityManager.getTransaction();

            entityTransaction.begin();
            entityTransaction.commit();

            Assertions.assertNotNull(entityManager, "EntityManager should not be null");
            Assertions.assertNotNull(entityTransaction, "EntityTransaction should not be null");

        } catch (RuntimeException e) {
            if (entityTransaction != null && entityTransaction.isActive()) {
                entityTransaction.rollback();
            }
            e.printStackTrace();
            Assertions.fail("Exception occurred: " + e.getMessage());
        } finally {

            if (entityManager != null) {
                entityManager.close();
            }
        }
    }

}
