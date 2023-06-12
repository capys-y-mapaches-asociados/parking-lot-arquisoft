import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TicketFormService } from './ticket-form.service';
import { TicketService } from '../service/ticket.service';
import { ITicket } from '../ticket.model';
import { IParkingSpot } from 'app/entities/parking-spot/parking-spot.model';
import { ParkingSpotService } from 'app/entities/parking-spot/service/parking-spot.service';

import { TicketUpdateComponent } from './ticket-update.component';

describe('Ticket Management Update Component', () => {
  let comp: TicketUpdateComponent;
  let fixture: ComponentFixture<TicketUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ticketFormService: TicketFormService;
  let ticketService: TicketService;
  let parkingSpotService: ParkingSpotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TicketUpdateComponent],
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
      .overrideTemplate(TicketUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TicketUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ticketFormService = TestBed.inject(TicketFormService);
    ticketService = TestBed.inject(TicketService);
    parkingSpotService = TestBed.inject(ParkingSpotService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ParkingSpot query and add missing value', () => {
      const ticket: ITicket = { id: 456 };
      const parkingSpotId: IParkingSpot = { id: 79450 };
      ticket.parkingSpotId = parkingSpotId;

      const parkingSpotCollection: IParkingSpot[] = [{ id: 72029 }];
      jest.spyOn(parkingSpotService, 'query').mockReturnValue(of(new HttpResponse({ body: parkingSpotCollection })));
      const additionalParkingSpots = [parkingSpotId];
      const expectedCollection: IParkingSpot[] = [...additionalParkingSpots, ...parkingSpotCollection];
      jest.spyOn(parkingSpotService, 'addParkingSpotToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      expect(parkingSpotService.query).toHaveBeenCalled();
      expect(parkingSpotService.addParkingSpotToCollectionIfMissing).toHaveBeenCalledWith(
        parkingSpotCollection,
        ...additionalParkingSpots.map(expect.objectContaining)
      );
      expect(comp.parkingSpotsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ticket: ITicket = { id: 456 };
      const parkingSpotId: IParkingSpot = { id: 92677 };
      ticket.parkingSpotId = parkingSpotId;

      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      expect(comp.parkingSpotsSharedCollection).toContain(parkingSpotId);
      expect(comp.ticket).toEqual(ticket);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicket>>();
      const ticket = { id: 123 };
      jest.spyOn(ticketFormService, 'getTicket').mockReturnValue(ticket);
      jest.spyOn(ticketService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ticket }));
      saveSubject.complete();

      // THEN
      expect(ticketFormService.getTicket).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ticketService.update).toHaveBeenCalledWith(expect.objectContaining(ticket));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicket>>();
      const ticket = { id: 123 };
      jest.spyOn(ticketFormService, 'getTicket').mockReturnValue({ id: null });
      jest.spyOn(ticketService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticket: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ticket }));
      saveSubject.complete();

      // THEN
      expect(ticketFormService.getTicket).toHaveBeenCalled();
      expect(ticketService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITicket>>();
      const ticket = { id: 123 };
      jest.spyOn(ticketService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ticket });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ticketService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareParkingSpot', () => {
      it('Should forward to parkingSpotService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(parkingSpotService, 'compareParkingSpot');
        comp.compareParkingSpot(entity, entity2);
        expect(parkingSpotService.compareParkingSpot).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
