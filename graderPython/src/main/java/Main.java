
import javax.jms.*;

import jdk.javadoc.internal.doclets.toolkit.util.SummaryAPIListBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import service.core.Result;
import service.core.Submission;

import static service.core.Result.corect;
import org.python.util.PythonInterpreter;

import java.util.concurrent.*;

public class Main {

    private static Submission judge(Submission submission){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<PyObject> future = executor.submit(new Callable<PyObject>() {
            @Override
            public PyObject call() throws Exception {
                PythonInterpreter interpreter = new PythonInterpreter();
                interpreter.exec(submission.code);

                PyObject[] pyArgs = new PyObject[]{new PyInteger(2), new PyInteger(3)};
                return interpreter.get("main").__call__(pyArgs);
            }
        });

        try {
            long timeoutInSeconds = 3;
            PyObject result = future.get(timeoutInSeconds, TimeUnit.SECONDS);

            // Check result
            if (result.asInt() == 5) {
                System.out.println("Test passed");
                submission.result = Result.corect;
            } else {
                submission.result = Result.incorect;
                System.out.println("Test failed");
            }
        } catch (TimeoutException e) {
            submission.result = Result.timeout;
            System.out.println("Test timed out");
        } catch (Exception e) {
            submission.result = Result.error;
            System.out.println("Error during test execution"+ e);
        } finally {
            executor.shutdownNow(); // Ensure the executor is properly shut down
        }

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