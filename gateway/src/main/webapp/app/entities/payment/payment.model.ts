import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

export interface IPayment {
  id: number;
  customerId?: string | null;
  reservationID?: string | null;
  amount?: number | null;
  paymentStatus?: PaymentStatus | null;
  paymentMethod?: PaymentMethod | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
