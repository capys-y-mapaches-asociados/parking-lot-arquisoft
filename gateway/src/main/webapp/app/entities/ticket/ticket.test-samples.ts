import dayjs from 'dayjs/esm';

import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  ticketCode: '6{6, 10}',
  issuedAt: dayjs('2023-06-11T03:59'),
  entryTime: dayjs('2023-06-10T18:42'),
  exitTime: dayjs('2023-06-10T07:42'),
  status: TicketStatus['ACTIVE'],
};

export const sampleWithPartialData: ITicket = {
  id: 7730,
  ticketCode: 'A{6, 10}',
  issuedAt: dayjs('2023-06-11T05:07'),
  entryTime: dayjs('2023-06-11T03:46'),
  exitTime: dayjs('2023-06-10T11:34'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithFullData: ITicket = {
  id: 94764,
  ticketCode: '1{6, 10}',
  issuedAt: dayjs('2023-06-11T02:49'),
  entryTime: dayjs('2023-06-10T16:25'),
  exitTime: dayjs('2023-06-10T17:05'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithNewData: NewTicket = {
  ticketCode: 'D{6, 10}',
  issuedAt: dayjs('2023-06-11T00:02'),
  entryTime: dayjs('2023-06-10T11:51'),
  exitTime: dayjs('2023-06-10T10:40'),
  status: TicketStatus['EXPIRED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
