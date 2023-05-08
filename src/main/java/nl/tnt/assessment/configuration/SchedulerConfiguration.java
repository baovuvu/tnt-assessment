package nl.tnt.assessment.configuration;

import nl.tnt.assessment.client.ClientScheduledJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@EnableScheduling
@Configuration
public class SchedulerConfiguration {

    @Value("${schedule.client.triggerInSeconds}")
    private int clientTriggerInSeconds;

    @Bean
    @Qualifier("clientQueueJob")
    public JobDetail clientQueueJob() {
        return JobBuilder.newJob().ofType(ClientScheduledJob.class)
            .storeDurably()
            .withIdentity("Qrtz_ClientQueueJob")
            .withDescription("Process client queues...")
            .build();
    }

    @Bean
    public Trigger clientQueueTrigger(@Qualifier("clientQueueJob") JobDetail job) {
        return TriggerBuilder.newTrigger().forJob(job)
            .withIdentity("Qrtz_ClientQueueTrigger")
            .withDescription("Trigger for ClientQueue job")
            .startNow()
            .withSchedule(simpleSchedule().repeatForever().withIntervalInSeconds(clientTriggerInSeconds))
            .build();
    }
}
