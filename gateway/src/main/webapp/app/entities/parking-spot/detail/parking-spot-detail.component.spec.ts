import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ParkingSpotDetailComponent } from './parking-spot-detail.component';

describe('ParkingSpot Management Detail Component', () => {
  let comp: ParkingSpotDetailComponent;
  let fixture: ComponentFixture<ParkingSpotDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ParkingSpotDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ parkingSpot: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ParkingSpotDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ParkingSpotDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load parkingSpot on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.parkingSpot).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
