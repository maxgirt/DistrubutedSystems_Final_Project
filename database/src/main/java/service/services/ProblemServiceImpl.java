package service.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.entities.ProblemEntity;
import service.repositories.ProblemRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ProblemServiceImpl implements ProblemService {

    private final ProblemRepository problemRepository;

    @Autowired
    public ProblemServiceImpl(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    @Override
    public List<ProblemEntity> getAllProblems() {
        return problemRepository.findAll();
    }

    @Override
    public ProblemEntity createOrUpdateProblem(ProblemEntity problem) {
        return problemRepository.save(problem);
    }

    @Override
    public ProblemEntity getProblemById(String id) {
        Optional<ProblemEntity> problem = problemRepository.findById(id);
        return problem.orElse(null); // return null if problem is not found
    }

    @Override
    public void deleteProblemById(String id) {
        problemRepository.deleteById(id);
    }

    @Override
    public Optional<ProblemEntity> findProblemByTitle(String title) {
        return problemRepository.findByTitle(title);
    }
        
}
