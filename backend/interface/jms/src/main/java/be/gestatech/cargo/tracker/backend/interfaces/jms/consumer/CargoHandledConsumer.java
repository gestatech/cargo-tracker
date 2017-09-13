package be.gestatech.cargo.tracker.backend.interfaces.jms.consumer;

import be.gestatech.cargo.tracker.backend.application.facade.api.CargoInspectionFacade;
import be.gestatech.cargo.tracker.backend.domain.model.vo.cargo.TrackingId;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "java:app/jms/CargoHandledQueue")
})
public class CargoHandledConsumer implements MessageListener {

    private static final Logger logger = Logger.getLogger(CargoHandledConsumer.class.getName());

    @Inject
    private CargoInspectionFacade cargoInspectionService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            String trackingIdString = textMessage.getText();
            cargoInspectionService.inspectCargo(new TrackingId(trackingIdString));
        } catch (JMSException e) {
            logger.log(Level.SEVERE, "Error procesing JMS message", e);
        }
    }
}
