package service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import service.database_entities.SubmissionEntity;


public interface SubmissionRepository extends MongoRepository<SubmissionEntity, String> {
    // Define custom query methods here if needed
    Optional<SubmissionEntity> findByIdUserAndIdProblem(String idUser, int idProblem);
}
