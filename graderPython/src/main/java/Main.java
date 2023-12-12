
import javax.jms.*;

import jdk.javadoc.internal.doclets.toolkit.util.SummaryAPIListBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import service.core.Submission;

public class Main {
    public static void main(String[] args) {
        try{

            //initialisations connections (diffenrent one for the liason with the client and service)
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            Connection connection = factory.createConnection();
            System.out.println("connection broker - broker message Ok");
            connection.setClientID("broker<->pythonGrader");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue submissions = session.createQueue("SUBMISSIONS");
            Queue results = session.createQueue("RESULTS");

            MessageConsumer consumer = session.createConsumer(submissions);
            MessageProducer producer = session.createProducer(results);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        Submission submission = (Submission) ((ObjectMessage) message).getObject();
                        String code = submission.code;
                        System.out.println("Code received " + code);
                        //SubmissionMessage submissionMessage = (SubmissionMessage) ((SubmissionMessage) messageSubmission).getObject();
                        //System.out.println("broker  : QuotationMessage recieve (token: "+messageSubmission.getToken()+")");
                        //service.evaluateSubmission()
                        // Message response = session.createObjectMessage(
                        //                        new ResultMessage(submissionMessage.getToken(),Result.corect, 42));
                        //producer.send(response);
                        //messageSubmission.acknowledge();
                    } catch (JMSException e) {
                        throw new RuntimeException(e);
                    }
                }


            });
        } catch (Exception e){
            System.out.println("error initialisations");
            e.printStackTrace();
        }
    }
}