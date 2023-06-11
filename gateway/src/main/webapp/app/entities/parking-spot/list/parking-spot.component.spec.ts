import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ParkingSpotService } from '../service/parking-spot.service';

import { ParkingSpotComponent } from './parking-spot.component';

describe('ParkingSpot Management Component', () => {
  let comp: ParkingSpotComponent;
  let fixture: ComponentFixture<ParkingSpotComponent>;
  let service: ParkingSpotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'parking-spot', component: ParkingSpotComponent }]), HttpClientTestingModule],
      declarations: [ParkingSpotComponent],
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
      .overrideTemplate(ParkingSpotComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParkingSpotComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ParkingSpotService);

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
    expect(comp.parkingSpots?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to parkingSpotService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getParkingSpotIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getParkingSpotIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
