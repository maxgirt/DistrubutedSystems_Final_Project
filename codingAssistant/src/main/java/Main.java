

import org.apache.activemq.ActiveMQConnectionFactory;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import service.core.Result;
import service.core.ResultFlag;
import service.core.Submission;
import service.core.TestCase;

import javax.jms.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.*;

import javax.jms.*;

import jdk.javadoc.internal.doclets.toolkit.util.SummaryAPIListBuilder;
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



public class Main {
    private static final String API_URL = "https://api-inference.huggingface.co/models/TinyLlama/TinyLlama-1.1B-Chat-v1.0";
    private static final String TOKEN = "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"; // Replace with your token


    public static void main(String[] args) {
        try{

            //ToDo: get the hostname and the grader_id and the programming language from the arguments
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("ai_assistant");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue requests = session.createQueue("AI_REQUESTS");

            MessageConsumer consumer = session.createConsumer(requests);
            MessageProducer producer = session.createProducer(null);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("Processing Message");

                    try {
                        Submission request = (Submission) ((ObjectMessage) message).getObject();


                        URL url = new URL(API_URL);

                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; utf-8");
                        conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
                        conn.setDoOutput(true);

                        String jsonInputString = "{\"inputs\": \"What might be wrong with this code? " +request.code + "\"}";

                        try(OutputStream os = conn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }

                        try(Scanner scanner = new Scanner(conn.getInputStream(), "UTF-8")) {
                            while(scanner.hasNextLine()) {
                                System.out.println(scanner.nextLine());
                            }
                        }
                       /* // Judge the submission
                        submission = judge(submission);

                        // Send the response back to the broker
                        message.acknowledge();
                        Message response = session.createObjectMessage(submission);
                        response.setJMSCorrelationID(String.valueOf(submission.id));
                        producer.send(message.getJMSReplyTo(), response);*/

                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    } catch (ProtocolException e) {
                        throw new RuntimeException(e);
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
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
