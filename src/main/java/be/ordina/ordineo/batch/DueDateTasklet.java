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

    @Scheduled(cron="0 3 * * * *")
    public void execute() throws Exception {
        List<Milestone> milestones = milestoneRepository.findAll();

        for (Milestone milestone : milestones) {
            if(milestone.getEndDate() == null && ((milestone.getDueDate().minusWeeks(2)).isEqual(LocalDate.now()) || (milestone.getDueDate().minusWeeks(1)).isEqual(LocalDate.now()) || (milestone.getDueDate().minusDays(1)).isEqual(LocalDate.now()))){
                log.info("Milestone with id: " + milestone.getId()+" is due soon");
                List<String> subscribers = new ArrayList<>();
                subscribers.add(milestone.getUsername());
                for (String subscriber : subscribers) {
                    publishMessage("Milestone from "+milestone.getUsername()+" for objective "+ milestone.getObjective().getTitle()+" is going to expire on "+milestone.getDueDate(),subscriber,"milestone");
                }
            }else{
                log.info("we're good!");
            }
        }
    }

    public void publishMessage(String message,String subscriber,String messageType)  throws Exception{
        String url = "https://notification-ordineo.cfapps.io/api/messages";
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
