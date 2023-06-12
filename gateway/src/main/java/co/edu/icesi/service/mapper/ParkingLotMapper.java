package co.edu.icesi.service.mapper;

import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.service.dto.ParkingLotDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ParkingLot} and its DTO {@link ParkingLotDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParkingLotMapper extends EntityMapper<ParkingLotDTO, ParkingLot> {}
