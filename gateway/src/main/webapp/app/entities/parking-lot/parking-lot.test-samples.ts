import { IParkingLot, NewParkingLot } from './parking-lot.model';

export const sampleWithRequiredData: IParkingLot = {
  id: 30092,
  name: 'Centers',
  location: 'Soft generating Fresh',
  capacity: 6555,
};

export const sampleWithPartialData: IParkingLot = {
  id: 59434,
  name: 'COM',
  location: 'Steel withdrawal',
  capacity: 4970,
};

export const sampleWithFullData: IParkingLot = {
  id: 1187,
  name: 'Borders Leu Tasty',
  location: 'Borders capacitor Rubber',
  capacity: 10173,
};

export const sampleWithNewData: NewParkingLot = {
  name: 'programming',
  location: '(Malvinas) Borders invoice',
  capacity: 11136,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
