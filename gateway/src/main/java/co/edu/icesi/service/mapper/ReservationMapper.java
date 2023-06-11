package co.edu.icesi.service.mapper;

import co.edu.icesi.domain.Customer;
import co.edu.icesi.domain.Notification;
import co.edu.icesi.domain.Reservation;
import co.edu.icesi.service.dto.CustomerDTO;
import co.edu.icesi.service.dto.NotificationDTO;
import co.edu.icesi.service.dto.ReservationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Reservation} and its DTO {@link ReservationDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReservationMapper extends EntityMapper<ReservationDTO, Reservation> {
    @Mapping(target = "customerId", source = "customerId", qualifiedByName = "customerId")
    @Mapping(target = "notifications", source = "notifications", qualifiedByName = "notificationId")
    ReservationDTO toDto(Reservation s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);

    @Named("notificationId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    NotificationDTO toDtoNotificationId(Notification notification);
}
