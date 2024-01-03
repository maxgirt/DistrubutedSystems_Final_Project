
import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import service.core.Result;
import service.core.ResultFlag;
import service.core.Submission;

import org.python.util.PythonInterpreter;
import service.core.TestCase;

import java.util.ArrayList;
import java.util.concurrent.*;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;



public class Main {

    private static class PythonTask implements Callable<PyObject> {
        private final String code;
        private final PyObject[] args;

        public PythonTask(String code, PyObject[] args) {
            this.code = code;
            this.args = args;
        }

        @Override
        public PyObject call() throws Exception {
            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.exec(code);
            return interpreter.get("main").__call__(args);
        }
    }

    private static ArrayList<TestCase> getTestCasesFromService(String idProblem) {
        String urlString = "http://localhost:8083/problems/" + idProblem + "/testcases"; 
        ArrayList<TestCase> testCases = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responsecode = conn.getResponseCode();
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {
                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }
                scanner.close();

                Gson gson = new Gson();
                testCases = gson.fromJson(inline, new TypeToken<ArrayList<TestCase>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testCases;
    }


    private static Submission judge(Submission submission){
        submission.results = new ArrayList<>();

        // Retrieve test cases from the database service
        ArrayList<TestCase> testCases = getTestCasesFromService(submission.idProblem);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        //ToDo: The test case execution could be parallelised
        for (TestCase testCase : testCases){
            PyObject[] pyArgs = new PyObject[]{new PyString(testCase.input)};
            PythonTask task = new PythonTask(submission.code, pyArgs);
            Future<PyObject> future = executor.submit(task);

            String testCaseInput;
            String testCaseOutput;
            if (testCase.hidden){
                testCaseInput = "Hidden Input";
                testCaseOutput = "Hidden Output";
            }else {
                testCaseInput = testCase.input;
                testCaseOutput = testCase.output;
            }
            try {
                long timeoutInSeconds = testCase.timeout;
                PyObject result = future.get(timeoutInSeconds, TimeUnit.SECONDS);
                String resultStr = (result == null) ? null : result.toString();

                // Check result
                String testCaseResult;
                if (testCase.hidden){
                    testCaseResult = "Hidden Result";
                }else {
                    testCaseResult = resultStr;
                }

                if (resultStr != null && resultStr.equals(testCase.output)) {
                    System.out.println("Test passed");
                    submission.results.add(new Result(testCaseInput, testCaseOutput, testCaseResult, ResultFlag.corect));
                } else {
                    submission.results.add(new Result(testCaseInput, testCaseOutput, testCaseResult, ResultFlag.incorect));
                    System.out.println("Test failed");
                }
            } catch (TimeoutException e) {
                submission.results.add(new Result(testCaseInput, testCaseOutput, e.toString(), ResultFlag.incorect));
                System.out.println("Test timed out");
            } catch (Exception e) {
                submission.results.add(new Result(testCaseInput, testCaseOutput, e.toString(), ResultFlag.incorect));
                System.out.println("Error during test execution"+ e);
            }
        }
        executor.shutdownNow();
        return submission;
    }


    public static void main(String[] args) {
        try{

            //ToDo: get the hostname and the grader_id and the programming language from the arguments
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("grader");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue submissions = session.createQueue("SUBMISSIONS_PYTHON");

            MessageConsumer consumer = session.createConsumer(submissions);
            MessageProducer producer = session.createProducer(null);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("Processing Message");
                    try {
                        Submission submission = (Submission) ((ObjectMessage) message).getObject();

                        // Judge the submission
                        submission = judge(submission);

                        // Send the response back to the broker
                        message.acknowledge();
                        Message response = session.createObjectMessage(submission);
                        response.setJMSCorrelationID(String.valueOf(submission.id));
                        producer.send(message.getJMSReplyTo(), response);

                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }


            });
            connection.start();
        } catch (Exception e){
            System.out.println("error initialisations");
            e.printStackTrace();
        }

    }
}