package service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.entities.SubmissionEntity;
import service.services.SubmissionService;

import java.util.List;

@RestController
@RequestMapping("/submissions") // all routes will be relative to /submissions
public class SubmissionController {

    private final SubmissionService submissionService;

    @Autowired // injects the service into the controller
    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<SubmissionEntity> createSubmission(@RequestBody SubmissionEntity submission) {
        SubmissionEntity newSubmission = submissionService.createOrUpdateSubmission(submission);
        return ResponseEntity.ok(newSubmission);
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
