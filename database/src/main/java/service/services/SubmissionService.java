package service.services;

import java.util.List;

import service.database_entities.SubmissionEntity;

public interface SubmissionService {
    SubmissionEntity createOrUpdateSubmission(SubmissionEntity submission);
    SubmissionEntity getSubmissionById(String id);
    List<SubmissionEntity> getAllSubmissions();
    void deleteSubmission(String id);
    void incrementUserTry(String idUser, int idProblem);
}
