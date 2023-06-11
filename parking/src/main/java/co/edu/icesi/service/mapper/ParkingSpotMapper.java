package co.edu.icesi.service.mapper;

import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.service.dto.ParkingLotDTO;
import co.edu.icesi.service.dto.ParkingSpotDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ParkingSpot} and its DTO {@link ParkingSpotDTO}.
 */
@Mapper(componentModel = "spring")
public interface ParkingSpotMapper extends EntityMapper<ParkingSpotDTO, ParkingSpot> {
    @Mapping(target = "parkingLotId", source = "parkingLotId", qualifiedByName = "parkingLotId")
    ParkingSpotDTO toDto(ParkingSpot s);

    @Named("parkingLotId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkingLotDTO toDtoParkingLotId(ParkingLot parkingLot);
}
