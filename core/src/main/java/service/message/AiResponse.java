package service.message;

public class AiResponse implements java.io.Serializable{
    private String response;
    private int id;
    public AiResponse(String response, int id){
        this.response = response;
        this.id = id;
    }

    public AiResponse(){};

    public String getRequest(){
        return this.response;
    }
    public int getId(){
        return this.id;
    }
}

