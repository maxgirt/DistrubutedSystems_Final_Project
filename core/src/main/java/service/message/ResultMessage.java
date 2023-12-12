package service.message;
import service.core.Result;
public class ResultMessage implements java.io.Serializable {
    private int token;
    private Result result;
    private int idSubmission;
    public ResultMessage(int token, Result result,int idSubmission) { 
        this.token = token;
        this.result = result;
        this.idSubmission = idSubmission;
    }
    public int getToken() { 
        return token; 
    } 
    public Result getResult() { 
        return result; 
    } 
    public int getIdSubmission() { 
        return idSubmission; 
    } 
}