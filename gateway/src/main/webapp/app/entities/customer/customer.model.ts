export interface ICustomer {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  password?: string | null;
}

export type NewCustomer = Omit<ICustomer, 'id'> & { id: null };
