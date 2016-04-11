package be.ordina.ordineo.model.projection;

import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.model.ObjectiveType;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

/**
 * Created by PhDa on 11/04/2016.
 */
@Projection(name = "objectiveView", types = {Objective.class})
public interface ObjectiveView {

    String getTitle();

    String getDescription();

    ObjectiveType getObjectiveType();

    List<String> getTags();

}
