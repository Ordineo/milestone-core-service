package be.ordina.ordineo.batch;

import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.repository.MilestoneRepository;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PhDa on 6/05/2016.
 */

@Slf4j
@Component
public class DueDateTasklet {

    @Autowired
    MilestoneRepository milestoneRepository;

    //Run every night at 3 am
    @Scheduled(cron = "0 0 3 * * *")
    public void execute() throws Exception {
        List<Milestone> milestones = milestoneRepository.findAll();

        for (Milestone milestone : milestones) {
            if (milestoneDueInTwoWeeks(milestone)
                    || milestoneDueInOneWeek(milestone)
                    || milestoneDueTomorrow(milestone)) {

                log.info(LocalDate.now() + ": Milestone with id: " + milestone.getId() + " is due in less than 2 weeks");
                List<String> subscribers = new ArrayList<>();
                subscribers.add(milestone.getUsername());
                for (String subscriber : subscribers) {
                    String message = "Milestone from " + milestone.getUsername() + " for objective " + milestone.getObjective().getTitle() + " is going to expire on " + milestone.getDueDate();
                    String messageType = "duedate"; //TODO
                    publishMessage(message, subscriber, messageType);
                }
            } else {
                log.info("we're good!");
            }
        }
    }

    private boolean milestoneDue(Milestone milestone, ChronoLocalDate due) {
        return milestone.getEndDate() == null
                && milestone.getDueDate().isEqual(due);
    }

    private boolean milestoneDueTomorrow(Milestone milestone) {
        return milestoneDue(milestone, LocalDate.now().plusDays(1));
    }

    private boolean milestoneDueInTwoWeeks(Milestone milestone) {
        return milestoneDue(milestone, LocalDate.now().plusWeeks(2));
    }

    private boolean milestoneDueInOneWeek(Milestone milestone) {
        return milestoneDue(milestone, LocalDate.now().plusWeeks(1));
    }

    public void publishMessage(String message, String subscriber, String messageType) throws Exception {
        String url = "http://localhost:1199/api/messages"; //TODO: url
        URL object = new URL(url);

        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestMethod("POST");

        JSONObject body = new JSONObject();
        body.put("message", message);
        body.put("subscriber", subscriber);
        body.put("messageType", messageType);

        OutputStreamWriter wr = null;
        try {
            wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(body.toString());
            wr.flush();
        } catch (IOException e) {
            log.error("Can't access notification service.");
        }

        //display what returns the POST request

        try {
            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                br.close();
            } else {

            }
        } catch (IOException e) {
            log.error("Can't access notification service.");
        }
    }
}
