import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../barrier.test-samples';

import { BarrierFormService } from './barrier-form.service';

describe('Barrier Form Service', () => {
  let service: BarrierFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BarrierFormService);
  });

  describe('Service methods', () => {
    describe('createBarrierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBarrierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            type: expect.any(Object),
            status: expect.any(Object),
            parkingLot: expect.any(Object),
          })
        );
      });

      it('passing IBarrier should create a new form with FormGroup', () => {
        const formGroup = service.createBarrierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            type: expect.any(Object),
            status: expect.any(Object),
            parkingLot: expect.any(Object),
          })
        );
      });
    });

    describe('getBarrier', () => {
      it('should return NewBarrier for default Barrier initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBarrierFormGroup(sampleWithNewData);

        const barrier = service.getBarrier(formGroup) as any;

        expect(barrier).toMatchObject(sampleWithNewData);
      });

      it('should return NewBarrier for empty Barrier initial value', () => {
        const formGroup = service.createBarrierFormGroup();

        const barrier = service.getBarrier(formGroup) as any;

        expect(barrier).toMatchObject({});
      });

      it('should return IBarrier', () => {
        const formGroup = service.createBarrierFormGroup(sampleWithRequiredData);

        const barrier = service.getBarrier(formGroup) as any;

        expect(barrier).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBarrier should not enable id FormControl', () => {
        const formGroup = service.createBarrierFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBarrier should disable id FormControl', () => {
        const formGroup = service.createBarrierFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
