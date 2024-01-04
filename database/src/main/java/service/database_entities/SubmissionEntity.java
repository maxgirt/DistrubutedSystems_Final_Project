package service.database_entities;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import service.core.ProgLanguage;
import service.core.Result;

@Document(collection = "submissions")
public class SubmissionEntity {

    @Id
    public String id;

    public String idProblem;
    public String code;
    public ProgLanguage progLanguage;
    public ArrayList<Result> result;
    public String idUser;
    public ArrayList<Result> results;
    public int userTry;

    // Constructor
    public SubmissionEntity() {
        // Default constructor
    }

    //get userTry
    public int getUserTry() {
        return userTry;
    }

    //set userTry
    public void setUserTry(int userTry) {
        this.userTry = userTry;
    }

    //get id
    public String getId() {
        return id;
    }

    //set id
    public void setId(String id) {
        this.id = id;
    }

    //get idProblem
    public String getIdProblem() {
        return idProblem;
    }

    //set idProblem
    public void setIdProblem(String idProblem) {
        this.idProblem = idProblem;
    }

    //get code
    public String getCode() {
        return code;
    }

    //set code
    public void setCode(String code) {
        this.code = code;
    }

    //get progLanguage
    public ProgLanguage getProgLanguage() {
        return progLanguage;
    }

    //set progLanguage
    public void setProgLanguage(ProgLanguage progLanguage) {
        this.progLanguage = progLanguage;
    }

    //get result
    public ArrayList<Result> getResult() {
        return result;
    }

    //set result
    public void setResults(ArrayList<Result> result) {
        this.result = result;
    }
}
