
package service.controllers;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.net.InetAddress;

import java.util.*;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import javax.jms.*;
import javax.jms.Queue;

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

import static service.core.ProgLanguage.python;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@RestController
public class ApplicationController {
    public final int PortDatabase = 8083;
    private Session session;
    Map<ProgLanguage, MessageProducer> submissionsQueueMap = new HashMap<>();


    // public SubmissionEntity convertToEntity(Submission submission) {
    //     SubmissionEntity submissionEntity = new SubmissionEntity();
    //     submissionEntity.setId(Integer.toString(submission.getId()));
    //     submissionEntity.setIdProblem(submission.getIdProblem());
    //     submissionEntity.setCode(submission.getCode());
    //     submissionEntity.setProgLanguage(submission.getProgLanguage());
    //     submissionEntity.setResults(submission.getResults());
    //     // Set other fields as necessary
    //     return submissionEntity;
    // }



    public ApplicationController() throws JMSException, UnknownHostException {
        ConnectionFactory factory =
                new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
        Connection connection = factory.createConnection();
        connection.setClientID("broker");
        this.session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Queue submissions_python = session.createQueue("SUBMISSIONS_PYTHON");
        Queue submissions_java = session.createQueue("SUBMISSIONS_JAVA");

        this.submissionsQueueMap.put(ProgLanguage.python, session.createProducer(submissions_python));
        this.submissionsQueueMap.put(ProgLanguage.java, session.createProducer(submissions_java));

        connection.start();
        System.out.println("Broker initialized");
    }

    @PostMapping(value="/submission", consumes="application/json")
    public ResponseEntity<Submission> submitSolution(@RequestBody Submission submission) throws JMSException {
        Queue tmpQueue = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(tmpQueue);
    
        System.out.println(submission.code);
        Message submissionMessage = this.session.createObjectMessage(submission);
        submissionMessage.setJMSReplyTo(tmpQueue);
        this.submissionsQueueMap.get(submission.progLanguage).send(submissionMessage);
    
        Message responseMessage = consumer.receive();
        responseMessage.acknowledge();
        Submission result = (Submission) ((ObjectMessage) responseMessage).getObject();
    
        // // Convert Submission to SubmissionEntity
        // SubmissionEntity submissionEntity = convertToEntity(result);
    
        // Send a POST request to the Submission Controller endpoint
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Submission> request = new HttpEntity<>(result, headers);
        
        // Change the URL to where your Submission Controller is running
        String submissionControllerUrl = "http://localhost:8083/submissions";
        ResponseEntity<String> submissionResponse = restTemplate.postForEntity(submissionControllerUrl, request, String.class);
    
        // Return the original Submission response or handle the error
        if (submissionResponse.getStatusCode().is2xxSuccessful()) {
            System.out.println("Submission stored successfully!");
            System.out.flush();
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } else {
            // Handle the error appropriately
            System.out.println("Failed to store the submission.");
            System.out.flush();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
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
    public ResponseEntity<List<Problem>> getProblem() {
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" +PortDatabase + "/problems";
        ResponseEntity<List<Problem>> response = template.exchange(
            urlService,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Problem>>() {}
        );
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