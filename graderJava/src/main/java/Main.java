import javax.jms.*;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.concurrent.*;
import org.apache.activemq.ActiveMQConnectionFactory;
import service.core.Result;
import service.core.ResultFlag;
import service.core.Submission;
import service.core.TestCase;

public class Main {

        
    private static class JavaTask implements Callable<String> {
        private final String code;
        private final String[] args;

        public JavaTask(String code, String[] args) {
            this.code = code;
            this.args = args;
        }

        @Override
        public String call() throws Exception {
            try {
                // Step 1: Save the code to a file (e.g., Main.java)
                String fileName = "Main.java";
                System.out.println("Saving code to file " + fileName);
                try (Writer writer = new FileWriter(fileName)) {
                    writer.write(code);
                }

                // Step 2: Compile the code using JavaCompiler
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                int compilationResult = compiler.run(null, null, null, fileName);

                // Check if compilation is successful
                if (compilationResult != 0) {
                    return "Compilation failed";
                }

                // Step 3: Execute the compiled class file and capture the output
                String className = fileName.replace(".java", "");
                Process process = Runtime.getRuntime().exec("java " + className);
                java.util.Scanner scanner = new java.util.Scanner(process.getInputStream()).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                // Clean up: Delete the generated .class file
                File classFile = new File(className + ".class");
                classFile.delete();

                return output;
            } catch (Exception e) {
                return "Error during Java code execution: " + e.getMessage();
            }
        }
    }

    private static Submission judge(Submission submission) {
        submission.results = new ArrayList<>();
        System.out.println("Judging submission " + submission.id);

        // ToDo: query test cases from the database
        ArrayList<TestCase> testCases = new ArrayList<>();
        testCases.add(new TestCase("3,2", "5", 1, 0));
        testCases.add(new TestCase("6,2", "8", 1, 0));
        testCases.add(new TestCase("14,0", "14", 1, 0));
        testCases.get(2).hidden = true;
        System.out.println("Test cases loaded");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // ToDo: The test case execution could be parallelized
        for (TestCase testCase : testCases) {
          //  System.out.println("Executing test case " + testCase.id);
            String[] javaArgs = new String[]{testCase.input};
            JavaTask task = new JavaTask(submission.code, javaArgs);
            Future<String> future = executor.submit(task);

            String testCaseInput;
            String testCaseOutput;
            if (testCase.hidden) {
                testCaseInput = "Hidden Input";
                testCaseOutput = "Hidden Output";
            } else {
                testCaseInput = testCase.input;
                testCaseOutput = testCase.output;
            }

            try {
                long timeoutInSeconds = testCase.timeout;
                String result = future.get(timeoutInSeconds, TimeUnit.SECONDS);

                // Check result
                String testCaseResult;
                if (testCase.hidden) {
                    testCaseResult = "Hidden Result";
                } else {
                    testCaseResult = result;
                }

                if (result != null && result.equals(testCase.output)) {
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
                System.out.println("Error during test execution" + e);
            }
        }
        executor.shutdownNow();
        return submission;
    }

    public static void main(String[] args) {
        try {
            // ToDo: get the hostname and the grader_id and the programming language from the arguments
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("grader");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue submissions = session.createQueue("SUBMISSIONS_JAVA");

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
        } catch (Exception e) {
            System.out.println("Error during initializations");
            e.printStackTrace();
        }
    }
}
