package service.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import service.entities.ProblemEntity;

public interface ProblemRepository extends MongoRepository<ProblemEntity, String> {
    // Define custom query methods here if needed
    public Optional<ProblemEntity> findByTitle(String title);
}

/* 
repositories serve as the data access layer for your application, 
providing us with CRUD operations and the ability to define custom queries
*/

/* default CRUD
    save(S entity): Save an entity. If the entity is new, it will be created; otherwise, it will be updated.
    findById(ID id): Retrieve an entity by its id.
    findAll(): Retrieve all entities of the repository type.
    findAllById(Iterable<ID> ids): Retrieve entities by a list of ids.
    deleteById(ID id): Delete an entity by id.
    delete(S entity): Delete a given entity.
    count(): Return the count of entities.
    existsById(ID id): Check if an entity exists by id.
 */