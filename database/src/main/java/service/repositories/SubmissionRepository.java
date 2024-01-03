package service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import service.entities.SubmissionEntity;


public interface SubmissionRepository extends MongoRepository<SubmissionEntity, String> {
    // Define custom query methods here if needed
    Optional<SubmissionEntity> findByIdUserAndIdProblem(String idUser, int idProblem);
}
