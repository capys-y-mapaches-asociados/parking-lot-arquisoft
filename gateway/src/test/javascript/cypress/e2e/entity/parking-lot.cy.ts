import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('ParkingLot e2e test', () => {
  const parkingLotPageUrl = '/parking-lot';
  const parkingLotPageUrlPattern = new RegExp('/parking-lot(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const parkingLotSample = {"name":"salmon SAS connect","location":"British Unions","capacity":12414};

  let parkingLot;
  // let parkingSpot;
  // let barrier;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/parking-spots',
      body: {"number":6249,"status":"RESERVED","spotType":"LOADING","spotVehicle":"CARGO_LARGE"},
    }).then(({ body }) => {
      parkingSpot = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/barriers',
      body: {"name":"compress York capacitor","type":"ENTRY","status":"DISABLED"},
    }).then(({ body }) => {
      barrier = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/parking-lots+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/parking-lots').as('postEntityRequest');
    cy.intercept('DELETE', '/api/parking-lots/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/parking-spots', {
      statusCode: 200,
      body: [parkingSpot],
    });

    cy.intercept('GET', '/api/barriers', {
      statusCode: 200,
      body: [barrier],
    });

  });
   */

  afterEach(() => {
    if (parkingLot) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/parking-lots/${parkingLot.id}`,
      }).then(() => {
        parkingLot = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (parkingSpot) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/parking-spots/${parkingSpot.id}`,
      }).then(() => {
        parkingSpot = undefined;
      });
    }
    if (barrier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/barriers/${barrier.id}`,
      }).then(() => {
        barrier = undefined;
      });
    }
  });
   */

  it('ParkingLots menu should load ParkingLots page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('parking-lot');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ParkingLot').should('exist');
    cy.url().should('match', parkingLotPageUrlPattern);
  });

  describe('ParkingLot page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(parkingLotPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ParkingLot page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/parking-lot/new$'));
        cy.getEntityCreateUpdateHeading('ParkingLot');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parkingLotPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/parking-lots',
          body: {
            ...parkingLotSample,
            parkingSpots: parkingSpot,
            parkingSpots: parkingSpot,
            barriers: barrier,
          },
        }).then(({ body }) => {
          parkingLot = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/parking-lots+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [parkingLot],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(parkingLotPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(parkingLotPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details ParkingLot page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('parkingLot');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parkingLotPageUrlPattern);
      });

      it('edit button click should load edit ParkingLot page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParkingLot');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parkingLotPageUrlPattern);
      });

      it('edit button click should load edit ParkingLot page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ParkingLot');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parkingLotPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of ParkingLot', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('parkingLot').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', parkingLotPageUrlPattern);

        parkingLot = undefined;
      });
    });
  });

  describe('new ParkingLot page', () => {
    beforeEach(() => {
      cy.visit(`${parkingLotPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ParkingLot');
    });

    it.skip('should create an instance of ParkingLot', () => {
      cy.get(`[data-cy="name"]`).type('blockchains').should('have.value', 'blockchains');

      cy.get(`[data-cy="location"]`).type('azure Somoni Ohio').should('have.value', 'azure Somoni Ohio');

      cy.get(`[data-cy="capacity"]`).type('1682').should('have.value', '1682');

      cy.get(`[data-cy="parkingSpots"]`).select([0]);
      cy.get(`[data-cy="parkingSpots"]`).select([0]);
      cy.get(`[data-cy="barriers"]`).select([0]);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        parkingLot = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', parkingLotPageUrlPattern);
    });
  });
});
