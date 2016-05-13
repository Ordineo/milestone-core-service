package be.ordina.ordineo.repository;

import be.ordina.ordineo.model.Milestone;
import be.ordina.ordineo.model.projection.MilestoneView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Created by gide on 11/04/16.
 */

@RepositoryRestResource(excerptProjection = MilestoneView.class)
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @RestResource(path="findByUsername",rel="findByUsername")
    @Query("select m from Milestone m where lower(m.username) = lower(:username) order by (CASE WHEN (m.endDate != null) THEN m.endDate ELSE m.dueDate END) desc")
    List<Milestone> findByUsernameOrderByDate(@Param("username") String username);

}
