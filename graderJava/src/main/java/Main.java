import service.core.Result;

import service.message.SubmissionMessage;
import service.message.ResultMessage;

import javax.jms.*;
import org.apache.activemq.ActiveMQConnectionFactory;


public class Main {
    public static void main(String[] args) {
        try{

            //initialisations connections (diffenrent one for the liason with the client and service)
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            Connection connection = factory.createConnection();
            System.out.println("connection broker - broker message Ok");
            connection.setClientID("broker<->client");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue offerQueue = session.createQueue("RESULT");
            Topic topic = session.createTopic("SUBMISSION");

            MessageConsumer consumer = session.createConsumer(topic);
            MessageProducer producer = session.createProducer(offerQueue);


            consumer.setMessageListener(new MessageListener() {
            @Override
                public void onMessage(Message messageSubmission) {
                    try{
                        SubmissionMessage submissionMessage = (SubmissionMessage) ((SubmissionMessage) messageSubmission).getObject();
                        System.out.println("broker  : QuotationMessage recieve (token: "+messageSubmission.getToken()+")");
                        //service.evaluateSubmission()
                        Message response = session.createObjectMessage(
                                                new ResultMessage(submissionMessage.getToken(),Result.corect, 42));
                        producer.send(response);
                        messageSubmission.acknowledge();
                    } catch (JMSException e) {
                        System.out.println("error recieve QuotationMessage");
                        e.printStackTrace();
                    }    
                }
            });       
        } catch (Exception e){
            System.out.println("error initialisations");
            e.printStackTrace();
        }
    }
}