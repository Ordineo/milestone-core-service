package be.ordina.ordineo.repository;

import be.ordina.ordineo.model.Objective;
import be.ordina.ordineo.model.projection.ObjectiveView;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Created by PhDa on 11/04/2016.
 */
@RepositoryRestResource(excerptProjection = ObjectiveView.class)
public interface ObjectiveRepository extends PagingAndSortingRepository<Objective,Long>{

    @RestResource(path="findByTitle", rel="findByTitle")
    Objective findByTitleIgnoreCase(@Param("title") String title);

}
