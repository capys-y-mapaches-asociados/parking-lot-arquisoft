import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ParkingLotDetailComponent } from './parking-lot-detail.component';

describe('ParkingLot Management Detail Component', () => {
  let comp: ParkingLotDetailComponent;
  let fixture: ComponentFixture<ParkingLotDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ParkingLotDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ parkingLot: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ParkingLotDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ParkingLotDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load parkingLot on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.parkingLot).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
