export interface IParkingLot {
  id: number;
  name?: string | null;
  location?: string | null;
  capacity?: number | null;
}

export type NewParkingLot = Omit<IParkingLot, 'id'> & { id: null };
