package nl.tnt.assessment.client;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientScheduledJob extends QuartzJobBean {

    private final List<Client> clients;

    public ClientScheduledJob(ShipmentClient shipmentClient, PricingClient pricingClient, TrackClient trackClient) {
        this.clients = List.of(shipmentClient, trackClient, pricingClient);
    }

    @Override
    protected void executeInternal(JobExecutionContext context) {
        clients.forEach(Client::processQueue);
    }
}
