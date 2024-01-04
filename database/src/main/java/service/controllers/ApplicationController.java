// package service.controllers;


// import java.util.Collection;
// import java.util.Map;
// import java.util.TreeMap;
// import java.util.ArrayList;

// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.web.client.RestTemplate;

// import java.net.InetAddress;
// import java.net.UnknownHostException;


// import service.core.Problem;
// import service.core.TestCase;
// import service.core.Submission;
// @RestController 
// public class ApplicationController {
//     //---------------------------------------------Problem
//     public static int idProblem;
//     private Map<String, Problem> problems = new TreeMap<>();

//     //@jsonview
//     @PostMapping(value="/problems", consumes="application/json") 
//     public ResponseEntity<Problem> createProblem( @RequestBody Problem problem) {

//         problem.id = idProblem ++;
//         problems.put(Integer.toString(problem.id),problem);
//         System.out.println(problem.testCases);

//         //debuging for problem TestCase
//         /*for(TestCase test: problem.testCases){
//             System.out.println(test.input+"-"+test.output);
//         }*/

//         String url = "http://"+getHost()+"/problems/" + problem.id; 
//         return ResponseEntity .status(HttpStatus.CREATED) .header("Location", url) 
//                               .header("Content-Location", url) .body(problem);
//     }

//    @GetMapping(value="/testcases/{problem_id}", produces="application/json")
//    public ResponseEntity<ArrayList<TestCase>> getTestCases() {
//        ArrayList<TestCase> list = new ArrayList<>();
//        // ToDo: Fetch the testcases from the databse and return them
//        return ResponseEntity.status(HttpStatus.OK).body(list);
//    }


//     @GetMapping(value="/problems", produces="application/json") 
//     public ResponseEntity<ArrayList<String>> getProblem() { 
//         ArrayList<String> list = new ArrayList<>(); 
//         for (Problem problem : problems.values()) {
//             list.add("http:" + getHost() + "/problems/"+problem.id); 
//         } 
//         return ResponseEntity.status(HttpStatus.OK).body(list); 
//     }


//     @GetMapping(value="/problems/{id}", produces={"application/json"})
//     public ResponseEntity<Problem> getProblem(@PathVariable String id) { 
//         Problem problem = problems.get(id); 
//         if (problem == null) { 
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
//         } 
//         return ResponseEntity.status(HttpStatus.OK).body(problem); 
//     }


//     @Value("${server.port}")
//     private int port;
//     private String getHost() {
//         try { 
//             return InetAddress.getLocalHost().getHostAddress() + ":" + port; 
//         } catch (UnknownHostException e) { 
//             return "localhost:" + port;
//         } 
//     }


// }