package service.services;
import java.util.List;
import java.util.Optional;

import service.database_entities.ProblemEntity;

public interface ProblemService {
    public List<ProblemEntity> getAllProblems();
    ProblemEntity createOrUpdateProblem(ProblemEntity problem);
    ProblemEntity getProblemById(String id);
    void deleteProblemById(String id);
    Optional<ProblemEntity> findProblemByTitle(String title);}
