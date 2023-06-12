package co.edu.icesi.service.mapper;

import co.edu.icesi.domain.Notification;
import co.edu.icesi.service.dto.NotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {}
