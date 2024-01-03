package service.core;

public class SubmissionRequest {

    public String idProblem; // Changed from int to String
    public String code;
    public ProgLanguage progLanguage;

    public SubmissionRequest(String idProblem, String code, ProgLanguage progLanguage, Result result) {
        this.idProblem = idProblem;
        this.code = code;
        this.progLanguage = progLanguage;
    }

    public SubmissionRequest() {}
}
