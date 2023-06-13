import dayjs from 'dayjs/esm';

import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  ticketCode: 'B18DBAAB6',
  issuedAt: dayjs('2023-06-12T08:09'),
  entryTime: dayjs('2023-06-12T03:01'),
  exitTime: dayjs('2023-06-12T14:13'),
  status: TicketStatus['ACTIVE'],
};

export const sampleWithPartialData: ITicket = {
  id: 57248,
  ticketCode: '4ADE668E',
  issuedAt: dayjs('2023-06-12T09:30'),
  entryTime: dayjs('2023-06-13T01:28'),
  exitTime: dayjs('2023-06-13T01:09'),
  status: TicketStatus['ACTIVE'],
};

export const sampleWithFullData: ITicket = {
  id: 3134,
  ticketCode: '862729A7D2',
  issuedAt: dayjs('2023-06-12T02:36'),
  entryTime: dayjs('2023-06-12T23:16'),
  exitTime: dayjs('2023-06-12T22:23'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithNewData: NewTicket = {
  ticketCode: '7CF75FA2F8',
  issuedAt: dayjs('2023-06-12T14:12'),
  entryTime: dayjs('2023-06-13T00:44'),
  exitTime: dayjs('2023-06-12T15:08'),
  status: TicketStatus['ACTIVE'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
