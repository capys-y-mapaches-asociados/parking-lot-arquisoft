import dayjs from 'dayjs/esm';
import { IParkingSpot } from 'app/entities/parking-spot/parking-spot.model';
import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

export interface ITicket {
  id: number;
  ticketCode?: string | null;
  issuedAt?: dayjs.Dayjs | null;
  parkingSpotId?: string | null;
  entryTime?: dayjs.Dayjs | null;
  exitTime?: dayjs.Dayjs | null;
  status?: TicketStatus | null;
  parkingSpotId?: Pick<IParkingSpot, 'id'> | null;
}

export type NewTicket = Omit<ITicket, 'id'> & { id: null };
