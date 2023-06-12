import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ParkingLotService } from '../service/parking-lot.service';

import { ParkingLotComponent } from './parking-lot.component';

describe('ParkingLot Management Component', () => {
  let comp: ParkingLotComponent;
  let fixture: ComponentFixture<ParkingLotComponent>;
  let service: ParkingLotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'parking-lot', component: ParkingLotComponent }]), HttpClientTestingModule],
      declarations: [ParkingLotComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ParkingLotComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParkingLotComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ParkingLotService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.parkingLots?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to parkingLotService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getParkingLotIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getParkingLotIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
