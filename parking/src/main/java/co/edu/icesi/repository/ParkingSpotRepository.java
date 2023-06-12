package co.edu.icesi.repository;

import co.edu.icesi.domain.ParkingSpot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ParkingSpot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {}
