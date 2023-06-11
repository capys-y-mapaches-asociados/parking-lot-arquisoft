import dayjs from 'dayjs/esm';
import { ICustomer } from 'app/entities/customer/customer.model';
import { INotification } from 'app/entities/notification/notification.model';
import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';

export interface IReservation {
  id: number;
  customerId?: string | null;
  parkingSpotId?: string | null;
  startTime?: dayjs.Dayjs | null;
  endTime?: dayjs.Dayjs | null;
  status?: ReservationStatus | null;
  reservationCode?: string | null;
  customerId?: Pick<ICustomer, 'id'> | null;
  customerId?: Pick<ICustomer, 'id'> | null;
  notifications?: Pick<INotification, 'id'> | null;
}

export type NewReservation = Omit<IReservation, 'id'> & { id: null };
