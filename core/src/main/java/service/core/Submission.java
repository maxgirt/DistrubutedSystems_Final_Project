package service.core;

import java.io.Serializable;
import java.util.ArrayList;

public class Submission implements Serializable {

    public int id;
    public String idProblem; // Changed from int to String
    public String code;
    public ProgLanguage progLanguage;
    public ArrayList<Result> results;

    public Submission(String idProblem, String code, ProgLanguage progLanguage) {
        this.idProblem = idProblem;
        this.code = code;
        this.progLanguage = progLanguage;
    }

    public Submission(String idProblem, String code, ProgLanguage progLanguage, Result result) {
        this.idProblem = idProblem;
        this.code = code;
        this.progLanguage = progLanguage;
    }

    public Submission(SubmissionRequest submissionRequest, int idSubmission, Result result) {
        this.idProblem = submissionRequest.idProblem; // Ensure SubmissionRequest.idProblem is also a String
        this.code = submissionRequest.code;
        this.progLanguage = submissionRequest.progLanguage;
    }

    public Submission() {}
}
