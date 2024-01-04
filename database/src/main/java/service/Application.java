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
                problem1.setTitle("Two Sum");
                problem1.setDescription("Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target");
                List<TestCase> testCases1 = new ArrayList<>();
                testCases1.add(new TestCase("[2,7,11,15],9", "[0,1]", defaultTimeout));
                testCases1.add(new TestCase("[1,2,3,4],8", "[]", defaultTimeout));
                testCases1.add(new TestCase("[1,3,2,4],6", "[1,3]", defaultTimeout));
                problem1.setTestCases(testCases1);
                ProblemService.createOrUpdateProblem(problem1);

                ProblemEntity problem2 = new ProblemEntity();
                problem2.setTitle("Find Maximum");
                problem2.setDescription("Given an array of integers, return the maximum value");
                List<TestCase> testCases2 = new ArrayList<>();
                testCases2.add(new TestCase("[1,3,5,7,9]", "9", defaultTimeout));
                testCases2.add(new TestCase("[-3,-1,-2,-4,-6]", "-1", defaultTimeout));
                testCases2.add(new TestCase("[7]", "7", defaultTimeout));
                problem2.setTestCases(testCases2);
                ProblemService.createOrUpdateProblem(problem2);
                
                ProblemEntity problem3 = new ProblemEntity();
                problem3.setTitle("Check Palindrome");
                problem3.setDescription("Given a string, return true if it is a palindrome, otherwise return false");
                List<TestCase> testCases3 = new ArrayList<>();
                testCases3.add(new TestCase("\"racecar\"", "true", defaultTimeout));
                testCases3.add(new TestCase("\"hello\"", "false", defaultTimeout));
                testCases3.add(new TestCase("\"\"", "true", defaultTimeout));
                problem3.setTestCases(testCases3);
                ProblemService.createOrUpdateProblem(problem3);

                ProblemEntity problem4 = new ProblemEntity();
                problem4.setTitle("Print Hello World");
                problem4.setDescription("Print \"Hello World\" to the console");
                List<TestCase> testCases4 = new ArrayList<>();
                testCases4.add(new TestCase("", "Hello World", defaultTimeout));
                problem4.setTestCases(testCases4);
                ProblemService.createOrUpdateProblem(problem4);

                ProblemEntity problem5 = new ProblemEntity();
                problem5.setTitle("Add Two Numbers");
                problem5.setDescription("Given two integers, return the sum of the two integers");
                List<TestCase> testCases5 = new ArrayList<>();
                testCases5.add(new TestCase("1,2", "3", defaultTimeout));
                testCases5.add(new TestCase("0,0", "0", defaultTimeout));
                testCases5.add(new TestCase("-1,1", "0", defaultTimeout));
                problem5.setTestCases(testCases5);
                ProblemService.createOrUpdateProblem(problem5);
                
                ProblemEntity problem6 = new ProblemEntity();
                problem6.setTitle("Return 0");
                problem6.setDescription("Return 0");
                List<TestCase> testCases6 = new ArrayList<>();
                testCases6.add(new TestCase("", "0", defaultTimeout));
                problem6.setTestCases(testCases6);
                ProblemService.createOrUpdateProblem(problem6);
                

                System.out.println("Problems created successfully");
            }
            else {
                System.out.println("Problems found in the database, skipping creation...");
            }
        };
    }


}