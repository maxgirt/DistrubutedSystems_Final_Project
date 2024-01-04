

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


            String qm_serverHost = System.getenv("QM_SERVER_HOST");
            if (qm_serverHost == null || qm_serverHost.isEmpty()) {
                qm_serverHost = "localhost";  // Default to localhost if not set (helpful for local testing)
            }
            String qm_port = System.getenv("QM_SERVER_PORT");
            if (qm_port == null || qm_port.isEmpty()) {
                qm_port = "61616";  // Default to localhost if not set (helpful for local testing)
            }
            String client_id = System.getenv("CLIENT_ID");
            if (client_id == null || client_id.isEmpty()) {
                client_id = "ai_assistant";  // Default to localhost if not set (helpful for local testing)
            }

            System.out.println(client_id+qm_port+qm_serverHost);

            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://"+qm_serverHost+":"+qm_port);
            Connection connection = factory.createConnection();
            connection.setClientID(client_id);
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
