package be.ordina.ordineo.model.projection;

import be.ordina.ordineo.model.Objective;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

/**
 * Created by gide on 14/04/16.
 */
@Projection(name = "objectiveTitleAndTagsView", types = {Objective.class})
public interface ObjectiveTitleAndTagsView {

    String getTitle();

    List<String> getTags();

}
