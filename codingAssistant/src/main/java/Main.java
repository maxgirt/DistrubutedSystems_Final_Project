

import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;
import java.io.IOException;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import service.message.AiRequest;
import service.message.AiResponse;



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
                        AiRequest request = (AiRequest) ((ObjectMessage) message).getObject();

                        URL url = new URL(API_URL);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json; utf-8");
                        conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
                        conn.setDoOutput(true);

                        String prompt = "Explain what this code does and why it might be wrong. ";
                        String jsonInputString = "{\"inputs\": \""+ prompt + " " +request.getRequest() + "\"}";

                        try(OutputStream os = conn.getOutputStream()) {
                            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                            os.write(input, 0, input.length);
                        }


                        StringBuilder response = new StringBuilder();
                        try (Scanner scanner = new Scanner(conn.getInputStream(), StandardCharsets.UTF_8.name())) {
                            while (scanner.hasNextLine()) {
                                response.append(scanner.nextLine());
                            }
                        }

                        String result = response.toString();
                        jsonInputString = jsonInputString.replaceFirst("inputs", "generated_text");
                        int lengthToRemove = jsonInputString.length();
                        String resultWithoutPrompt = result.substring(lengthToRemove);

                        // Send the response back to the broker
                        AiResponse aiResponse = new AiResponse(resultWithoutPrompt, request.getId());
                        message.acknowledge();
                        Message aiResponseMessage = session.createObjectMessage(aiResponse);
                        aiResponseMessage.setJMSCorrelationID(String.valueOf(request.getId()));
                        producer.send(message.getJMSReplyTo(), aiResponseMessage);

                    } catch (JMSException | IOException e) {
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
