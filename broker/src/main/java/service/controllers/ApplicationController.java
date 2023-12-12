package service.controllers;


import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;


import service.core.Problem;
import service.core.TestCase;
import service.core.Submission;
import service.core.ProgLanguage;
import service.core.SubmissionRequest;
import service.message.SubmissionMessage;
import service.message.ResultMessage;
import java.io.Serializable;



@RestController 
public class ApplicationController {
    public final int PortDatabase = 8083;
    //---------------------------------------------Problem
    @PostMapping(value="/problems", consumes="application/json") 
    public ResponseEntity<Problem> createProblem(@RequestBody Problem problem) {     
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" +PortDatabase + "/problems";
        try{
            ResponseEntity<Problem> response = template.postForEntity(urlService, problem, Problem.class);
            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                problem = response.getBody();
            }else{
                //return ResponseEntity.status(HttpStatus.NOT_FOUND).body();  
            }
        }catch(Exception e){
            System.out.println("problem acces database service ");
            //return ResponseEntity .status(HttpStatus.NOT_FOUND).body();  
        }
        String url = "http://"+getHost()+"/problems/" + problem.id; 
        //applications.put(Integer.toString(application.id), application);
        return ResponseEntity .status(HttpStatus.CREATED) .header("Location", url) 
                              .header("Content-Location", url) .body(problem);
    }


    @GetMapping(value="/problems", produces="application/json") 
    public ResponseEntity<ArrayList<String>> getProblem() { 
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" +PortDatabase + "/problems";
        ResponseEntity<ArrayList<String>> response = template.getForEntity(urlService,(Class<ArrayList<String>>) new ArrayList<String>().getClass());
        return ResponseEntity.status(HttpStatus.OK).body(response.getBody()); 
    }

    @GetMapping(value="/problems/{id}", produces={"application/json"})
    public ResponseEntity<Problem> getProblem(@PathVariable String id) { 
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" + PortDatabase + "/problems/"+id;
        try{
            ResponseEntity<Problem> response = template.getForEntity(urlService,Problem.class);
            return response; 
        }catch(HttpClientErrorException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
        }
    }







    /*
    //---------------------------------------------Submission
    public static final int portJavaGrader = 61616;
    public static final int portPythonGrader = 61616;
    public static  int token =0;
    @PostMapping(value="/Submissions", consumes="application/json") 
    public ResponseEntity<Submission> createSubmission(@RequestBody SubmissionRequest submissionRequest) {     
        //call grader service
        if(submissionRequest.progLanguage == ProgLanguage.java){
            port = portJavaGrader;
        }else if(submissionRequest.progLanguage == ProgLanguage.python){
            port = portJavaGrader;
        }else{
            //return ResponseEntity .status(HttpStatus.BAD_REQUEST).body();
        }
        ResultMessage resultMessage;
        try{
            //initialisations conncetion to broker message
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://localhost:"+port);
            Connection connection = factory.createConnection();
            connection.setClientID("broker");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Queue queue = session.createQueue("RESULT");
            Topic topic = session.createTopic("SUBMISSION");
            MessageConsumer consumer = session.createConsumer(queue);
            MessageProducer producer = session.createProducer(topic);
            connection.start();
            

            //send all ClientMessage
            producer.send(
                session.createObjectMessage(
                    new SubmissionMessage(token++, submissionRequest.idProblem,submissionRequest.code)
                )
            );
            System.out.println("message send");

            Message message = consumer.receive();
            resultMessage =(ResultMessage) ((ResultMessage) message).getObject();
            System.out.println("message recieve");

            message.acknowledge();
            //clean connections
            connection.close();
        } catch (JMSException e) {
            System.out.println("error listener");
            e.printStackTrace();
        }
        Submission submission = new Submission(submissionRequest,resultMessage.getIdSubmission(),resultMessage.getResult(),token);
        String url = "http://"+getHost()+"/submissions/" + submission.id; 
        //applications.put(Integer.toString(application.id), application);
        return ResponseEntity .status(HttpStatus.CREATED) .header("Location", url) 
                              .header("Content-Location", url) .body(submission);
    }
*/

    /*
    @GetMapping(value="/submissions", produces="application/json") 
    public ResponseEntity<ArrayList<String>> getSubmission() { 
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" +PortDatabase + "/Submissions";
        ResponseEntity<ArrayList<String>> response = template.getForEntity(urlService,(Class<ArrayList<String>>) new ArrayList<String>().getClass());
        return ResponseEntity.status(HttpStatus.OK).body(response.getBody()); 
    }

    @GetMapping(value="/submissions/{id}", produces={"application/json"})
    public ResponseEntity<Problem> getProblem(@PathVariable String id) { 
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" + PortDatabase + "/problems/"+id;
        try{
            ResponseEntity<Problem> response = template.getForEntity(urlService,Problem.class);
            return response; 
        }catch(HttpClientErrorException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
        }
    }*/


    @Value("${server.port}")
    private int port;
    private String getHost() {
        
        try { 
            return InetAddress.getLocalHost().getHostAddress() + ":" + port; 
        } catch (UnknownHostException e) { 
            return "localhost:" + port;
        } 
    }
}