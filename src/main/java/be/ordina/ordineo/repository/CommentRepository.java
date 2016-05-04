package be.ordina.ordineo.repository;

import be.ordina.ordineo.model.Comment;
import be.ordina.ordineo.model.projection.CommentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by PhDa on 4/05/2016.
 */
@RepositoryRestResource(excerptProjection = CommentView.class)
public interface CommentRepository extends JpaRepository<Comment,Long>{

}
