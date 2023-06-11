import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ParkingLotFormService } from './parking-lot-form.service';
import { ParkingLotService } from '../service/parking-lot.service';
import { IParkingLot } from '../parking-lot.model';

import { ParkingLotUpdateComponent } from './parking-lot-update.component';

describe('ParkingLot Management Update Component', () => {
  let comp: ParkingLotUpdateComponent;
  let fixture: ComponentFixture<ParkingLotUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let parkingLotFormService: ParkingLotFormService;
  let parkingLotService: ParkingLotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ParkingLotUpdateComponent],
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
      .overrideTemplate(ParkingLotUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParkingLotUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parkingLotFormService = TestBed.inject(ParkingLotFormService);
    parkingLotService = TestBed.inject(ParkingLotService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const parkingLot: IParkingLot = { id: 456 };

      activatedRoute.data = of({ parkingLot });
      comp.ngOnInit();

      expect(comp.parkingLot).toEqual(parkingLot);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParkingLot>>();
      const parkingLot = { id: 123 };
      jest.spyOn(parkingLotFormService, 'getParkingLot').mockReturnValue(parkingLot);
      jest.spyOn(parkingLotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parkingLot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parkingLot }));
      saveSubject.complete();

      // THEN
      expect(parkingLotFormService.getParkingLot).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(parkingLotService.update).toHaveBeenCalledWith(expect.objectContaining(parkingLot));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParkingLot>>();
      const parkingLot = { id: 123 };
      jest.spyOn(parkingLotFormService, 'getParkingLot').mockReturnValue({ id: null });
      jest.spyOn(parkingLotService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parkingLot: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parkingLot }));
      saveSubject.complete();

      // THEN
      expect(parkingLotFormService.getParkingLot).toHaveBeenCalled();
      expect(parkingLotService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IParkingLot>>();
      const parkingLot = { id: 123 };
      jest.spyOn(parkingLotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parkingLot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parkingLotService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
