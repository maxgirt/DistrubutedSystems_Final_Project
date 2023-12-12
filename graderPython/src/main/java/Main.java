
import javax.jms.*;

import jdk.javadoc.internal.doclets.toolkit.util.SummaryAPIListBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import service.core.Result;
import service.core.Submission;

import static service.core.Result.corect;

public class Main {
    public static void main(String[] args) {
        try{

            //initialisations connections (diffenrent one for the liason with the client and service)
            ConnectionFactory factory =
                    new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("grader");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue submissions = session.createQueue("SUBMISSIONS");
            Queue results = session.createQueue("RESULTS");

            MessageConsumer consumer = session.createConsumer(submissions);
            MessageProducer producer = session.createProducer(results);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    System.out.println("Processing Message");
                    try {
                        Submission submission = (Submission) ((ObjectMessage) message).getObject();
                        String code = submission.code;
                        System.out.println("Code received " + code);
                        //SubmissionMessage submissionMessage = (SubmissionMessage) ((SubmissionMessage) messageSubmission).getObject();
                        //System.out.println("broker  : QuotationMessage recieve (token: "+messageSubmission.getToken()+")");
                        //service.evaluateSubmission()
                        submission.result = corect;
                        Message response = session.createObjectMessage(submission);
                        producer.send(response);
                        //messageSubmission.acknowledge();
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