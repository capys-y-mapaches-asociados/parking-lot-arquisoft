import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../parking-spot.test-samples';

import { ParkingSpotFormService } from './parking-spot-form.service';

describe('ParkingSpot Form Service', () => {
  let service: ParkingSpotFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ParkingSpotFormService);
  });

  describe('Service methods', () => {
    describe('createParkingSpotFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createParkingSpotFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            number: expect.any(Object),
            status: expect.any(Object),
            spotType: expect.any(Object),
            spotVehicle: expect.any(Object),
            parkingLotId: expect.any(Object),
            parkingLotId: expect.any(Object),
          })
        );
      });

      it('passing IParkingSpot should create a new form with FormGroup', () => {
        const formGroup = service.createParkingSpotFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            number: expect.any(Object),
            status: expect.any(Object),
            spotType: expect.any(Object),
            spotVehicle: expect.any(Object),
            parkingLotId: expect.any(Object),
            parkingLotId: expect.any(Object),
          })
        );
      });
    });

    describe('getParkingSpot', () => {
      it('should return NewParkingSpot for default ParkingSpot initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createParkingSpotFormGroup(sampleWithNewData);

        const parkingSpot = service.getParkingSpot(formGroup) as any;

        expect(parkingSpot).toMatchObject(sampleWithNewData);
      });

      it('should return NewParkingSpot for empty ParkingSpot initial value', () => {
        const formGroup = service.createParkingSpotFormGroup();

        const parkingSpot = service.getParkingSpot(formGroup) as any;

        expect(parkingSpot).toMatchObject({});
      });

      it('should return IParkingSpot', () => {
        const formGroup = service.createParkingSpotFormGroup(sampleWithRequiredData);

        const parkingSpot = service.getParkingSpot(formGroup) as any;

        expect(parkingSpot).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IParkingSpot should not enable id FormControl', () => {
        const formGroup = service.createParkingSpotFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewParkingSpot should disable id FormControl', () => {
        const formGroup = service.createParkingSpotFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
