import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ReservationFormService } from './reservation-form.service';
import { ReservationService } from '../service/reservation.service';
import { IReservation } from '../reservation.model';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';
import { INotification } from 'app/entities/notification/notification.model';
import { NotificationService } from 'app/entities/notification/service/notification.service';

import { ReservationUpdateComponent } from './reservation-update.component';

describe('Reservation Management Update Component', () => {
  let comp: ReservationUpdateComponent;
  let fixture: ComponentFixture<ReservationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let reservationFormService: ReservationFormService;
  let reservationService: ReservationService;
  let customerService: CustomerService;
  let notificationService: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ReservationUpdateComponent],
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
      .overrideTemplate(ReservationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ReservationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    reservationFormService = TestBed.inject(ReservationFormService);
    reservationService = TestBed.inject(ReservationService);
    customerService = TestBed.inject(CustomerService);
    notificationService = TestBed.inject(NotificationService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customer query and add missing value', () => {
      const reservation: IReservation = { id: 456 };
      const customerId: ICustomer = { id: 11998 };
      reservation.customerId = customerId;
      const customerId: ICustomer = { id: 92899 };
      reservation.customerId = customerId;

      const customerCollection: ICustomer[] = [{ id: 84339 }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customerId, customerId];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(
        customerCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Notification query and add missing value', () => {
      const reservation: IReservation = { id: 456 };
      const notifications: INotification = { id: 52631 };
      reservation.notifications = notifications;

      const notificationCollection: INotification[] = [{ id: 49616 }];
      jest.spyOn(notificationService, 'query').mockReturnValue(of(new HttpResponse({ body: notificationCollection })));
      const additionalNotifications = [notifications];
      const expectedCollection: INotification[] = [...additionalNotifications, ...notificationCollection];
      jest.spyOn(notificationService, 'addNotificationToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      expect(notificationService.query).toHaveBeenCalled();
      expect(notificationService.addNotificationToCollectionIfMissing).toHaveBeenCalledWith(
        notificationCollection,
        ...additionalNotifications.map(expect.objectContaining)
      );
      expect(comp.notificationsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const reservation: IReservation = { id: 456 };
      const customerId: ICustomer = { id: 33878 };
      reservation.customerId = customerId;
      const customerId: ICustomer = { id: 10319 };
      reservation.customerId = customerId;
      const notifications: INotification = { id: 2622 };
      reservation.notifications = notifications;

      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      expect(comp.customersSharedCollection).toContain(customerId);
      expect(comp.customersSharedCollection).toContain(customerId);
      expect(comp.notificationsSharedCollection).toContain(notifications);
      expect(comp.reservation).toEqual(reservation);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservation>>();
      const reservation = { id: 123 };
      jest.spyOn(reservationFormService, 'getReservation').mockReturnValue(reservation);
      jest.spyOn(reservationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservation }));
      saveSubject.complete();

      // THEN
      expect(reservationFormService.getReservation).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(reservationService.update).toHaveBeenCalledWith(expect.objectContaining(reservation));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservation>>();
      const reservation = { id: 123 };
      jest.spyOn(reservationFormService, 'getReservation').mockReturnValue({ id: null });
      jest.spyOn(reservationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservation: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: reservation }));
      saveSubject.complete();

      // THEN
      expect(reservationFormService.getReservation).toHaveBeenCalled();
      expect(reservationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IReservation>>();
      const reservation = { id: 123 };
      jest.spyOn(reservationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ reservation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(reservationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCustomer', () => {
      it('Should forward to customerService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customerService, 'compareCustomer');
        comp.compareCustomer(entity, entity2);
        expect(customerService.compareCustomer).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareNotification', () => {
      it('Should forward to notificationService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(notificationService, 'compareNotification');
        comp.compareNotification(entity, entity2);
        expect(notificationService.compareNotification).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
