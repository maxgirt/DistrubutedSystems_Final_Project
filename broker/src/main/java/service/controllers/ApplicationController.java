
package service.controllers;

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;
import java.net.InetAddress;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import javax.jms.*;
import javax.jms.Queue;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.activemq.ActiveMQConnectionFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;


import service.core.Problem;
import service.core.TestCase;
import service.core.Submission;
import service.core.ProgLanguage;
import service.core.SubmissionRequest;
import service.message.AiRequest;
import service.message.AiResponse;
import service.message.SubmissionMessage;
import service.message.ResultMessage;
import java.io.Serializable;

import static service.core.ProgLanguage.python;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = {"http://localhost","http://localhost:8080", "http://127.0.0.1"})
public class ApplicationController {
    public final int PortDatabase = 8083;
    private Session session;
    Map<ProgLanguage, MessageProducer> submissionsQueueMap = new HashMap<>();
    private MessageProducer aiQueue;

    public ApplicationController() throws JMSException, UnknownHostException {
        ConnectionFactory factory =
                new ActiveMQConnectionFactory("failover://tcp://activemq:61616");
        Connection connection = factory.createConnection();
        System.out.println("GOT CONNECTION");
        connection.setClientID("broker");
        this.session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

        Queue submissions_python = session.createQueue("SUBMISSIONS_PYTHON");
        Queue submissions_java = session.createQueue("SUBMISSIONS_JAVA");

        this.submissionsQueueMap.put(ProgLanguage.python, session.createProducer(submissions_python));
        this.submissionsQueueMap.put(ProgLanguage.java, session.createProducer(submissions_java));

        Queue ai_requests = session.createQueue("AI_REQUESTS");
        this.aiQueue = session.createProducer(ai_requests);

        connection.start();
        System.out.println("Broker initialized");
    }

    @PostMapping(value = "/ai_assistance", consumes = "application/json")
    public ResponseEntity<AiResponse> request_assistance(@RequestBody AiRequest request) throws JMSException{
        Queue tmpQueue = session.createTemporaryQueue();
        MessageConsumer consumer = session.createConsumer(tmpQueue);

        System.out.println(request.getRequest());
        Message aiRequestMessage = this.session.createObjectMessage(request);
        aiRequestMessage.setJMSReplyTo(tmpQueue);

        this.aiQueue.send(aiRequestMessage);

        Message responseMessage = consumer.receive();
        responseMessage.acknowledge();
        AiResponse result = (AiResponse) ((ObjectMessage) responseMessage).getObject();

        return ResponseEntity.status(HttpStatus.OK).body(result);
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

        return ResponseEntity.status(HttpStatus.OK).body(result);
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
    public ResponseEntity<ArrayList<String>> getProblem() { 
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" + PortDatabase + "/problems";
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