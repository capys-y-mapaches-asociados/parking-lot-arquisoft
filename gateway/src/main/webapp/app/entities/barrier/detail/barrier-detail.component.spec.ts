import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BarrierDetailComponent } from './barrier-detail.component';

describe('Barrier Management Detail Component', () => {
  let comp: BarrierDetailComponent;
  let fixture: ComponentFixture<BarrierDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BarrierDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ barrier: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BarrierDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BarrierDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load barrier on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.barrier).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
