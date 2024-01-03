package service.services;
import service.entities.ProblemEntity;
import java.util.List;
import java.util.Optional;

public interface ProblemService {
    public List<ProblemEntity> getAllProblems();
    ProblemEntity createOrUpdateProblem(ProblemEntity problem);
    ProblemEntity getProblemById(String id);
    void deleteProblemById(String id);
    Optional<ProblemEntity> findProblemByTitle(String title);}
