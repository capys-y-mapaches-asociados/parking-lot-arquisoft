package co.edu.icesi.repository;

import co.edu.icesi.domain.ParkingLot;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ParkingLot entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {}
