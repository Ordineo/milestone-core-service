package be.ordina.ordineo.model.projection;

import be.ordina.ordineo.model.Milestone;
import org.springframework.data.rest.core.config.Projection;
import java.time.LocalDate;


/**
 * Created by gide on 11/04/16.
 */
@Projection(name = "milestoneView", types = {Milestone.class})
public interface MilestoneView {

    String getUsername();

    ObjectiveView getObjective();

    //List<CommentView> getComments();

    LocalDate getCreateDate();

    LocalDate getDueDate();

    LocalDate getEndDate();

    String getMoreInformation();


}
