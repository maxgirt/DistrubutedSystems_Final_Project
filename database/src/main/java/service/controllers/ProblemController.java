package service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.entities.ProblemEntity;
import service.services.ProblemService;

import java.util.List;

@RestController
@RequestMapping("/problems")
public class ProblemController {

    private final ProblemService problemService;

    @Autowired
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping
    public ResponseEntity<ProblemEntity> createProblem(@RequestBody ProblemEntity problem) {
        ProblemEntity newProblem = problemService.createOrUpdateProblem(problem);
        return ResponseEntity.ok(newProblem);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemEntity> getProblem(@PathVariable String id) {
        ProblemEntity problem = problemService.getProblemById(id);
        return problem != null ? ResponseEntity.ok(problem) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ProblemEntity>> getAllProblems() {
        List<ProblemEntity> problems = problemService.getAllProblems();
        return ResponseEntity.ok(problems);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemEntity> updateProblem(@PathVariable String id, @RequestBody ProblemEntity problem) {
        ProblemEntity existingProblem = problemService.getProblemById(id);
        if (existingProblem == null) {
            return ResponseEntity.notFound().build();
        }
        // Set the ID of the problem to ensure it matches the path variable
        problem.setId(id);
        ProblemEntity updatedProblem = problemService.createOrUpdateProblem(problem);
        return ResponseEntity.ok(updatedProblem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable String id) {
        problemService.deleteProblemById(id);
        return ResponseEntity.ok().build();
    }
}
