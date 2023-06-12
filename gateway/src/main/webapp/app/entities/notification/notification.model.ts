import dayjs from 'dayjs/esm';

export interface INotification {
  id: number;
  message?: string | null;
  sentAt?: dayjs.Dayjs | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
