import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ParkingSpotFormService } from './parking-spot-form.service';
import { ParkingSpotService } from '../service/parking-spot.service';
import { IParkingSpot } from '../parking-spot.model';
import { IParkingLot } from 'app/entities/parking-lot/parking-lot.model';
import { ParkingLotService } from 'app/entities/parking-lot/service/parking-lot.service';

import { ParkingSpotUpdateComponent } from './parking-spot-update.component';

describe('ParkingSpot Management Update Component', () => {
  let comp: ParkingSpotUpdateComponent;
  let fixture: ComponentFixture<ParkingSpotUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let parkingSpotFormService: ParkingSpotFormService;
  let parkingSpotService: ParkingSpotService;
  let parkingLotService: ParkingLotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ParkingSpotUpdateComponent],
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
      .overrideTemplate(ParkingSpotUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParkingSpotUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parkingSpotFormService = TestBed.inject(ParkingSpotFormService);
    parkingSpotService = TestBed.inject(ParkingSpotService);
    parkingLotService = TestBed.inject(ParkingLotService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ParkingLot query and add missing value', () => {
      const parkingSpot: IParkingSpot = { id: 456 };
      const parkingLotId: IParkingLot = { id: 88905 };
      parkingSpot.parkingLotId = parkingLotId;

      const parkingLotCollection: IParkingLot[] = [{ id: 30960 }];
      jest.spyOn(parkingLotService, 'query').mockReturnValue(of(new HttpResponse({ body: parkingLotCollection })));
      const additionalParkingLots = [parkingLotId];
      const expectedCollection: IParkingLot[] = [...additionalParkingLots, ...parkingLotCollection];
      jest.spyOn(parkingLotService, 'addParkingLotToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ parkingSpot });
      comp.ngOnInit();

      expect(parkingLotService.query).toHaveBeenCalled();
      expect(parkingLotService.addParkingLotToCollectionIfMissing).toHaveBeenCalledWith(
        parkingLotCollection,
        ...additionalParkingLots.map(expect.objectContaining)
      );
      expect(comp.parkingLotsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const parkingSpot: IParkingSpot = { id: 456 };
      const parkingLotId: IParkingLot = { id: 55218 };
      parkingSpot.parkingLotId = parkingLotId;

      activatedRoute.data = of({ parkingSpot });
      comp.ngOnInit();

      expect(comp.parkingLotsSharedCollection).toContain(parkingLotId);
      expect(comp.parkingSpot).toEqual(parkingSpot);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParkingSpot>>();
      const parkingSpot = { id: 123 };
      jest.spyOn(parkingSpotFormService, 'getParkingSpot').mockReturnValue(parkingSpot);
      jest.spyOn(parkingSpotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parkingSpot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parkingSpot }));
      saveSubject.complete();

      // THEN
      expect(parkingSpotFormService.getParkingSpot).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(parkingSpotService.update).toHaveBeenCalledWith(expect.objectContaining(parkingSpot));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParkingSpot>>();
      const parkingSpot = { id: 123 };
      jest.spyOn(parkingSpotFormService, 'getParkingSpot').mockReturnValue({ id: null });
      jest.spyOn(parkingSpotService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parkingSpot: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parkingSpot }));
      saveSubject.complete();

      // THEN
      expect(parkingSpotFormService.getParkingSpot).toHaveBeenCalled();
      expect(parkingSpotService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParkingSpot>>();
      const parkingSpot = { id: 123 };
      jest.spyOn(parkingSpotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parkingSpot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parkingSpotService.update).toHaveBeenCalled();
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
