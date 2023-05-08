package nl.tnt.assessment.client;

import org.quartz.JobExecutionContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.List;

@SuppressWarnings("rawtypes")
@Service
public class ClientScheduledJob extends QuartzJobBean {

    private final List<Client> clients;

    public ClientScheduledJob(ShipmentClient shipmentClient, PricingClient pricingClient, TrackClient trackClient) {
        this.clients = List.of(shipmentClient, trackClient, pricingClient);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void executeInternal(JobExecutionContext context) {
        clients.forEach(Client::processQueue);
    }
}
