package service.database_controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import service.core.Submission;
import service.database_entities.SubmissionEntity;
import service.services.SubmissionService;

import java.util.List;

@RestController
@RequestMapping("/submissions") // all routes will be relative to /submissions
public class SubmissionController {

    public SubmissionEntity convertToEntity(Submission submission) {
        SubmissionEntity submissionEntity = new SubmissionEntity();
        submissionEntity.setId(Integer.toString(submission.getId()));
        submissionEntity.setIdProblem(submission.getIdProblem());
        submissionEntity.setCode(submission.getCode());
        submissionEntity.setProgLanguage(submission.getProgLanguage());
        submissionEntity.setResults(submission.getResults());
        // Set other fields as necessary
        System.out.println("SubmissionEntity: " + submissionEntity);
        return submissionEntity;
    }

    


    private final SubmissionService submissionService;

    @Autowired // injects the service into the controller
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<String> createSubmission(@RequestBody Submission submission) {
        SubmissionEntity new_entity = convertToEntity(submission);
        //

        SubmissionEntity newSubmission = submissionService.createOrUpdateSubmission(new_entity);
        return ResponseEntity.ok("Submission created successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubmissionEntity> getSubmission(@PathVariable String id) {
        SubmissionEntity submission = submissionService.getSubmissionById(id);
        return submission != null ? ResponseEntity.ok(submission) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<SubmissionEntity>> getAllSubmissions() {
        List<SubmissionEntity> submissions = submissionService.getAllSubmissions();
        return ResponseEntity.ok(submissions);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubmission(@PathVariable String id) {
        submissionService.deleteSubmission(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/incrementUserTry")
    public ResponseEntity<Void> incrementUserTry(@RequestParam String idUser, @RequestParam int idProblem) {
        submissionService.incrementUserTry(idUser, idProblem);
        return ResponseEntity.ok().build();
    }
}
