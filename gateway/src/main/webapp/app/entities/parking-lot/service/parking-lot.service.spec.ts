import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IParkingLot } from '../parking-lot.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../parking-lot.test-samples';

import { ParkingLotService } from './parking-lot.service';

const requireRestSample: IParkingLot = {
  ...sampleWithRequiredData,
};

describe('ParkingLot Service', () => {
  let service: ParkingLotService;
  let httpMock: HttpTestingController;
  let expectedResult: IParkingLot | IParkingLot[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ParkingLotService);
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

    it('should create a ParkingLot', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const parkingLot = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(parkingLot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ParkingLot', () => {
      const parkingLot = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(parkingLot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ParkingLot', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ParkingLot', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ParkingLot', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addParkingLotToCollectionIfMissing', () => {
      it('should add a ParkingLot to an empty array', () => {
        const parkingLot: IParkingLot = sampleWithRequiredData;
        expectedResult = service.addParkingLotToCollectionIfMissing([], parkingLot);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parkingLot);
      });

      it('should not add a ParkingLot to an array that contains it', () => {
        const parkingLot: IParkingLot = sampleWithRequiredData;
        const parkingLotCollection: IParkingLot[] = [
          {
            ...parkingLot,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addParkingLotToCollectionIfMissing(parkingLotCollection, parkingLot);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ParkingLot to an array that doesn't contain it", () => {
        const parkingLot: IParkingLot = sampleWithRequiredData;
        const parkingLotCollection: IParkingLot[] = [sampleWithPartialData];
        expectedResult = service.addParkingLotToCollectionIfMissing(parkingLotCollection, parkingLot);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parkingLot);
      });

      it('should add only unique ParkingLot to an array', () => {
        const parkingLotArray: IParkingLot[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const parkingLotCollection: IParkingLot[] = [sampleWithRequiredData];
        expectedResult = service.addParkingLotToCollectionIfMissing(parkingLotCollection, ...parkingLotArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const parkingLot: IParkingLot = sampleWithRequiredData;
        const parkingLot2: IParkingLot = sampleWithPartialData;
        expectedResult = service.addParkingLotToCollectionIfMissing([], parkingLot, parkingLot2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(parkingLot);
        expect(expectedResult).toContain(parkingLot2);
      });

      it('should accept null and undefined values', () => {
        const parkingLot: IParkingLot = sampleWithRequiredData;
        expectedResult = service.addParkingLotToCollectionIfMissing([], null, parkingLot, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(parkingLot);
      });

      it('should return initial array if no ParkingLot is added', () => {
        const parkingLotCollection: IParkingLot[] = [sampleWithRequiredData];
        expectedResult = service.addParkingLotToCollectionIfMissing(parkingLotCollection, undefined, null);
        expect(expectedResult).toEqual(parkingLotCollection);
      });
    });

    describe('compareParkingLot', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareParkingLot(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareParkingLot(entity1, entity2);
        const compareResult2 = service.compareParkingLot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareParkingLot(entity1, entity2);
        const compareResult2 = service.compareParkingLot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareParkingLot(entity1, entity2);
        const compareResult2 = service.compareParkingLot(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
