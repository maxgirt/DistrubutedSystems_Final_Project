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

import java.net.InetAddress;
import java.net.UnknownHostException;


import service.core.Problem;
@RestController 
public class ApplicationController {
    //---------------------------------------------Problem
    @PostMapping(value="/problems", consumes="application/json") 
    public ResponseEntity<Problem> createProblem(@RequestBody Problem problem) {     
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" +8083 + "/problems";
        try{
            ResponseEntity<Problem> response = template.postForEntity(urlService, problem, Problem.class);
            if (response.getStatusCode().equals(HttpStatus.CREATED)) {
                problem = response.getBody();
            }
        }catch(Exception e){
            System.out.println("problem acces database service ");
        }
        String url = "http://"+getHost()+"/problems/" + problem.id; 
        //applications.put(Integer.toString(application.id), application);
        return ResponseEntity .status(HttpStatus.CREATED) .header("Location", url) 
                              .header("Content-Location", url) .body(problem);
    }


    @GetMapping(value="/problems", produces="problems/json") 
    public ResponseEntity<ArrayList<String>> getProblem() { 
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" +8083 + "/problems";
        ResponseEntity<ArrayList<String>> response = template.getForEntity(urlService,(Class<ArrayList<String>>) new ArrayList<String>().getClass());
        return ResponseEntity.status(HttpStatus.OK).body(response.getBody()); 
    }

    @GetMapping(value="/problems/{id}", produces={"application/json"})
    public ResponseEntity<Problem> getProblem(@PathVariable String id) { 
        RestTemplate template = new RestTemplate();
        String urlService = "http://localhost:" +8083 + "/problems/"+id;
        ResponseEntity<Problem> response = template.getForEntity(urlService,Problem.class);
        return response; 
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