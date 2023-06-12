package co.edu.icesi.repository;

import co.edu.icesi.domain.Barrier;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Barrier entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BarrierRepository extends JpaRepository<Barrier, Long> {}
