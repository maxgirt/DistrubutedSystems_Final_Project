package service.services;

import service.entities.SubmissionEntity;
import java.util.List;

public interface SubmissionService {
    SubmissionEntity createOrUpdateSubmission(SubmissionEntity submission);
    SubmissionEntity getSubmissionById(String id);
    List<SubmissionEntity> getAllSubmissions();
    void deleteSubmission(String id);
    void incrementUserTry(String idUser, int idProblem);
}
