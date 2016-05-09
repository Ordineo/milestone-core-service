package be.ordina.ordineo.batch;

import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.repository.MilestoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by PhDa on 6/05/2016.
 */

@Component
public class DueDateTasklet {

    @Autowired
    MilestoneRepository milestoneRepository;

    @Scheduled(cron="0 3 * * * *")
    public void execute() throws Exception {
        System.out.println(milestoneRepository);
        List<Milestone> milestones = milestoneRepository.findAll();

        for (Milestone milestone : milestones) {
            if(milestone.getEndDate() == null && (milestone.getDueDate().minusWeeks(2)).isBefore(LocalDate.now())){
                System.out.println("Milestone with id: " + milestone.getId()+" is due in less than 2 weeks");
            }else{
                System.out.println("we're good!");
            }
        }

    }

}
