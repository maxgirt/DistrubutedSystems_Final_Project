package service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.entities.SubmissionEntity;
import service.repositories.SubmissionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;

    @Autowired
    public SubmissionServiceImpl(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    @Override
    public SubmissionEntity createOrUpdateSubmission(SubmissionEntity submission) {
        //include logic to handle user tries or other business rules before saving
        return submissionRepository.save(submission);
    }

    @Override
    public SubmissionEntity getSubmissionById(String id) {
        Optional<SubmissionEntity> submission = submissionRepository.findById(id);
        return submission.orElse(null);
    }

    @Override
    public List<SubmissionEntity> getAllSubmissions() {
        return submissionRepository.findAll();
    }

    @Override
    public void deleteSubmission(String id) {
        submissionRepository.deleteById(id);
    }

    @Override
    public void incrementUserTry(String idUser, int idProblem) {
        // Find the submission by user ID and problem ID
        // Here you can define a custom query in the SubmissionRepository to find the submission
        // For example: findByUserIdAndProblemId(idUser, idProblem);
        Optional<SubmissionEntity> submission = submissionRepository.findByIdUserAndIdProblem(idUser, idProblem);

        submission.ifPresent(sub -> {
            sub.setUserTry(sub.getUserTry() + 1);
            submissionRepository.save(sub);
        });
    }
}
