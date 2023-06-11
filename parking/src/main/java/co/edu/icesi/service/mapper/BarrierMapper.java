package co.edu.icesi.service.mapper;

import co.edu.icesi.domain.Barrier;
import co.edu.icesi.domain.ParkingLot;
import co.edu.icesi.service.dto.BarrierDTO;
import co.edu.icesi.service.dto.ParkingLotDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Barrier} and its DTO {@link BarrierDTO}.
 */
@Mapper(componentModel = "spring")
public interface BarrierMapper extends EntityMapper<BarrierDTO, Barrier> {
    @Mapping(target = "parkingLot", source = "parkingLot", qualifiedByName = "parkingLotId")
    BarrierDTO toDto(Barrier s);

    @Named("parkingLotId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkingLotDTO toDtoParkingLotId(ParkingLot parkingLot);
}
