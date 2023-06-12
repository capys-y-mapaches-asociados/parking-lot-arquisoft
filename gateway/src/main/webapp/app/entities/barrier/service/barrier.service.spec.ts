import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBarrier } from '../barrier.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../barrier.test-samples';

import { BarrierService } from './barrier.service';

const requireRestSample: IBarrier = {
  ...sampleWithRequiredData,
};

describe('Barrier Service', () => {
  let service: BarrierService;
  let httpMock: HttpTestingController;
  let expectedResult: IBarrier | IBarrier[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BarrierService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Barrier', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const barrier = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(barrier).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Barrier', () => {
      const barrier = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(barrier).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Barrier', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Barrier', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Barrier', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBarrierToCollectionIfMissing', () => {
      it('should add a Barrier to an empty array', () => {
        const barrier: IBarrier = sampleWithRequiredData;
        expectedResult = service.addBarrierToCollectionIfMissing([], barrier);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(barrier);
      });

      it('should not add a Barrier to an array that contains it', () => {
        const barrier: IBarrier = sampleWithRequiredData;
        const barrierCollection: IBarrier[] = [
          {
            ...barrier,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBarrierToCollectionIfMissing(barrierCollection, barrier);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Barrier to an array that doesn't contain it", () => {
        const barrier: IBarrier = sampleWithRequiredData;
        const barrierCollection: IBarrier[] = [sampleWithPartialData];
        expectedResult = service.addBarrierToCollectionIfMissing(barrierCollection, barrier);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(barrier);
      });

      it('should add only unique Barrier to an array', () => {
        const barrierArray: IBarrier[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const barrierCollection: IBarrier[] = [sampleWithRequiredData];
        expectedResult = service.addBarrierToCollectionIfMissing(barrierCollection, ...barrierArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const barrier: IBarrier = sampleWithRequiredData;
        const barrier2: IBarrier = sampleWithPartialData;
        expectedResult = service.addBarrierToCollectionIfMissing([], barrier, barrier2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(barrier);
        expect(expectedResult).toContain(barrier2);
      });

      it('should accept null and undefined values', () => {
        const barrier: IBarrier = sampleWithRequiredData;
        expectedResult = service.addBarrierToCollectionIfMissing([], null, barrier, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(barrier);
      });

      it('should return initial array if no Barrier is added', () => {
        const barrierCollection: IBarrier[] = [sampleWithRequiredData];
        expectedResult = service.addBarrierToCollectionIfMissing(barrierCollection, undefined, null);
        expect(expectedResult).toEqual(barrierCollection);
      });
    });

    describe('compareBarrier', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBarrier(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBarrier(entity1, entity2);
        const compareResult2 = service.compareBarrier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBarrier(entity1, entity2);
        const compareResult2 = service.compareBarrier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBarrier(entity1, entity2);
        const compareResult2 = service.compareBarrier(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
