package be.ordina.ordineo.repository;

import be.ordina.ordineo.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by gide on 11/04/16.
 */
public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
}
