package service.entities;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import service.core.ProgLanguage;
import service.core.Result;

@Document(collection = "submissions")
public class SubmissionEntity {

    @Id
    private String id;

    public int idProblem;
    public String code;
    public ProgLanguage progLanguage;
    public Result result;
    public String idUser;
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
}
