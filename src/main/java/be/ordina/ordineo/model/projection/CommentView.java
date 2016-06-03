package be.ordina.ordineo.model.projection;

import be.ordina.ordineo.model.Comment;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by PhDa on 26/04/2016.
 */
@Projection(name = "commentView", types = {Comment.class})
public interface CommentView {

    String getUsername();

    String getMessage();

    LocalDateTime getCreateDate();

}
