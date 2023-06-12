import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BarrierFormService } from './barrier-form.service';
import { BarrierService } from '../service/barrier.service';
import { IBarrier } from '../barrier.model';
import { IParkingLot } from 'app/entities/parking-lot/parking-lot.model';
import { ParkingLotService } from 'app/entities/parking-lot/service/parking-lot.service';

import { BarrierUpdateComponent } from './barrier-update.component';

describe('Barrier Management Update Component', () => {
  let comp: BarrierUpdateComponent;
  let fixture: ComponentFixture<BarrierUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let barrierFormService: BarrierFormService;
  let barrierService: BarrierService;
  let parkingLotService: ParkingLotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BarrierUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BarrierUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BarrierUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    barrierFormService = TestBed.inject(BarrierFormService);
    barrierService = TestBed.inject(BarrierService);
    parkingLotService = TestBed.inject(ParkingLotService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ParkingLot query and add missing value', () => {
      const barrier: IBarrier = { id: 456 };
      const parkingLot: IParkingLot = { id: 16841 };
      barrier.parkingLot = parkingLot;

      const parkingLotCollection: IParkingLot[] = [{ id: 9604 }];
      jest.spyOn(parkingLotService, 'query').mockReturnValue(of(new HttpResponse({ body: parkingLotCollection })));
      const additionalParkingLots = [parkingLot];
      const expectedCollection: IParkingLot[] = [...additionalParkingLots, ...parkingLotCollection];
      jest.spyOn(parkingLotService, 'addParkingLotToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ barrier });
      comp.ngOnInit();

      expect(parkingLotService.query).toHaveBeenCalled();
      expect(parkingLotService.addParkingLotToCollectionIfMissing).toHaveBeenCalledWith(
        parkingLotCollection,
        ...additionalParkingLots.map(expect.objectContaining)
      );
      expect(comp.parkingLotsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const barrier: IBarrier = { id: 456 };
      const parkingLot: IParkingLot = { id: 81524 };
      barrier.parkingLot = parkingLot;

      activatedRoute.data = of({ barrier });
      comp.ngOnInit();

      expect(comp.parkingLotsSharedCollection).toContain(parkingLot);
      expect(comp.barrier).toEqual(barrier);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBarrier>>();
      const barrier = { id: 123 };
      jest.spyOn(barrierFormService, 'getBarrier').mockReturnValue(barrier);
      jest.spyOn(barrierService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ barrier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: barrier }));
      saveSubject.complete();

      // THEN
      expect(barrierFormService.getBarrier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(barrierService.update).toHaveBeenCalledWith(expect.objectContaining(barrier));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBarrier>>();
      const barrier = { id: 123 };
      jest.spyOn(barrierFormService, 'getBarrier').mockReturnValue({ id: null });
      jest.spyOn(barrierService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ barrier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: barrier }));
      saveSubject.complete();

      // THEN
      expect(barrierFormService.getBarrier).toHaveBeenCalled();
      expect(barrierService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBarrier>>();
      const barrier = { id: 123 };
      jest.spyOn(barrierService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ barrier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(barrierService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareParkingLot', () => {
      it('Should forward to parkingLotService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(parkingLotService, 'compareParkingLot');
        comp.compareParkingLot(entity, entity2);
        expect(parkingLotService.compareParkingLot).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
