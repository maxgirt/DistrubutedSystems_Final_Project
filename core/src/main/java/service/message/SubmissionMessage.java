package service.message;
public class SubmissionMessage implements java.io.Serializable {
    private int token;
    private int idProblem; 
    private String code; 
    public SubmissionMessage(int token, int idProblem, String code) { 
        this.token = token;
        this.idProblem = idProblem;
        this.code = code;
    } 

    public int getToken() { 
        return token; 
    } 
    public int getIdProblem() { 
        return idProblem; 
    } 
    public String getCode() { 
        return code;
    }
}