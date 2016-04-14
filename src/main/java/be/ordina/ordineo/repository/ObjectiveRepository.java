package be.ordina.ordineo.repository;

import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.model.projection.ObjectiveView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

/**
 * Created by PhDa on 11/04/2016.
 */
@RepositoryRestResource(excerptProjection = ObjectiveView.class)
public interface ObjectiveRepository extends JpaRepository<Objective,Long> {

    @RestResource(path="findByTitleOrTags", rel="findByTitleOrTags")
    @Query("select DISTINCT o from Objective o join o.tags t where lower(t) LIKE CONCAT('%',lower(:text),'%') or lower(o.title) LIKE CONCAT('%',lower(:text),'%') ")
    List<Objective> findByTitleOrTags(@Param("text") String text);

}
