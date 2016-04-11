package be.ordina.ordineo.repository;

import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.projection.MilestoneView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * Created by gide on 11/04/16.
 */
@RepositoryRestResource(excerptProjection = MilestoneView.class)
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

}
