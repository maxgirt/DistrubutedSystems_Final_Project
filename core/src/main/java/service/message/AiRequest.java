package service.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Random;

public class AiRequest implements java.io.Serializable{
    private String request;
    private int id;
    public AiRequest(String request){
        this.request = request;
        this.id = (new Random()).nextInt(1000000);
    }


    @JsonProperty("request")
    public void setRequest(String request){
        this.request = request;
    }
    public String getRequest(){
        return this.request;
    }
    public int getId(){
        return this.id;
    }
    public AiRequest(){};
}

