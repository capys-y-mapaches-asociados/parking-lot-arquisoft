import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../parking-lot.test-samples';

import { ParkingLotFormService } from './parking-lot-form.service';

describe('ParkingLot Form Service', () => {
  let service: ParkingLotFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ParkingLotFormService);
  });

  describe('Service methods', () => {
    describe('createParkingLotFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createParkingLotFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            location: expect.any(Object),
            capacity: expect.any(Object),
          })
        );
      });

      it('passing IParkingLot should create a new form with FormGroup', () => {
        const formGroup = service.createParkingLotFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            location: expect.any(Object),
            capacity: expect.any(Object),
          })
        );
      });
    });

    describe('getParkingLot', () => {
      it('should return NewParkingLot for default ParkingLot initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createParkingLotFormGroup(sampleWithNewData);

        const parkingLot = service.getParkingLot(formGroup) as any;

        expect(parkingLot).toMatchObject(sampleWithNewData);
      });

      it('should return NewParkingLot for empty ParkingLot initial value', () => {
        const formGroup = service.createParkingLotFormGroup();

        const parkingLot = service.getParkingLot(formGroup) as any;

        expect(parkingLot).toMatchObject({});
      });

      it('should return IParkingLot', () => {
        const formGroup = service.createParkingLotFormGroup(sampleWithRequiredData);

        const parkingLot = service.getParkingLot(formGroup) as any;

        expect(parkingLot).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IParkingLot should not enable id FormControl', () => {
        const formGroup = service.createParkingLotFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewParkingLot should disable id FormControl', () => {
        const formGroup = service.createParkingLotFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
