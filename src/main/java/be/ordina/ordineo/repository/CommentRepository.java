package be.ordina.ordineo.repository;

import be.ordina.ordineo.model.Comment;
import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.projection.CommentView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by PhDa on 4/05/2016.
 */
@RepositoryRestResource(excerptProjection = CommentView.class)
public interface CommentRepository extends PagingAndSortingRepository<Comment,Long> {

    @RestResource(path="findCommentsByMilestone",rel="findCommentsByMilestone")
    @Query("select c from Comment c where c.milestone = lower(:milestone) order by c.createDate desc")
    List<Comment> findByMilestoneOrderByDate(@Param("milestone") Milestone milestone);

}
