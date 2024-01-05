package service;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import service.core.TestCase;
import service.database_entities.ProblemEntity;
import service.services.ProblemService;

@SpringBootApplication 
public class Application{

    @Autowired
    private ProblemService ProblemService;

    public static void main(String[] args) { 
        SpringApplication.run(Application.class, args); 
    }

    
    @Bean
    CommandLineRunner init() {
        return args -> {

            int defaultTimeout = 10000; //1000 ms = 1 second
            

            //check to see if there is existing problems in the database
            if(ProblemService.getAllProblems().size() == 0) {
                System.out.println("No problems found in the database, creating new problems...");
                //populate the database with problems

                ProblemEntity problem1 = new ProblemEntity();
                problem1.setTitle("Hello World");
                problem1.setDescription("Return \"Hello World\"");
                List<TestCase> testCases1 = new ArrayList<>();
                testCases1.add(new TestCase("", "Hello World", defaultTimeout));
                problem1.setTestCases(testCases1);
                ProblemService.createOrUpdateProblem(problem1);

                ProblemEntity problem2 = new ProblemEntity();
                problem2.setTitle("Add Two Numbers");
                problem2.setDescription("Given two integers, return the sum of the two integers");
                List<TestCase> testCases2 = new ArrayList<>();
                testCases2.add(new TestCase("1,2", "3", defaultTimeout));
                testCases2.add(new TestCase("0,0", "0", defaultTimeout));
                testCases2.add(new TestCase("-1,1", "0", defaultTimeout));
                problem2.setTestCases(testCases2);
                ProblemService.createOrUpdateProblem(problem2);
                
                ProblemEntity problem3 = new ProblemEntity();
                problem3.setTitle("Return 0");
                problem3.setDescription("Return 0");
                List<TestCase> testCases3 = new ArrayList<>();
                testCases3.add(new TestCase("", "0", defaultTimeout));
                problem3.setTestCases(testCases3);
                ProblemService.createOrUpdateProblem(problem3);
                

                System.out.println("Problems created successfully");
            }
            else {
                System.out.println("Problems found in the database, skipping creation...");
            }
        };
    }


}