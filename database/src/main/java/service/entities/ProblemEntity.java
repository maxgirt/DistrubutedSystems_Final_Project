package service.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import service.core.TestCase;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "problems")
public class ProblemEntity {

    @Id
    private String id; // For internal use by MongoDB

    private String title;
    private String description;
    private List<TestCase> testCases;

    // Constructor
    public ProblemEntity() {
        // Default constructor
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }
}
