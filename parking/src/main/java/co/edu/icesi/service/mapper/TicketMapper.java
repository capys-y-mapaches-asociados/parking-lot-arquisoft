package co.edu.icesi.service.mapper;

import co.edu.icesi.domain.ParkingSpot;
import co.edu.icesi.domain.Ticket;
import co.edu.icesi.service.dto.ParkingSpotDTO;
import co.edu.icesi.service.dto.TicketDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Ticket} and its DTO {@link TicketDTO}.
 */
@Mapper(componentModel = "spring")
public interface TicketMapper extends EntityMapper<TicketDTO, Ticket> {
    @Mapping(target = "parkingSpotId", source = "parkingSpotId", qualifiedByName = "parkingSpotId")
    TicketDTO toDto(Ticket s);

    @Named("parkingSpotId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ParkingSpotDTO toDtoParkingSpotId(ParkingSpot parkingSpot);
}
