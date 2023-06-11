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

describe('Barrier e2e test', () => {
  const barrierPageUrl = '/barrier';
  const barrierPageUrlPattern = new RegExp('/barrier(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const barrierSample = {"name":"Awesome port","type":"EXIT","status":"MAINTENANCE"};

  let barrier;
  // let parkingLot;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/parking-lots',
      body: {"name":"system-worthy","location":"Fresh invoice bricks-and-clicks","capacity":2728},
    }).then(({ body }) => {
      parkingLot = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/barriers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/barriers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/barriers/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/parking-lots', {
      statusCode: 200,
      body: [parkingLot],
    });

  });
   */

  afterEach(() => {
    if (barrier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/barriers/${barrier.id}`,
      }).then(() => {
        barrier = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
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
   */

  it('Barriers menu should load Barriers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('barrier');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Barrier').should('exist');
    cy.url().should('match', barrierPageUrlPattern);
  });

  describe('Barrier page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(barrierPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Barrier page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/barrier/new$'));
        cy.getEntityCreateUpdateHeading('Barrier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', barrierPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/barriers',
          body: {
            ...barrierSample,
            parkingLot: parkingLot,
          },
        }).then(({ body }) => {
          barrier = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/barriers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [barrier],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(barrierPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(barrierPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Barrier page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('barrier');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', barrierPageUrlPattern);
      });

      it('edit button click should load edit Barrier page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Barrier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', barrierPageUrlPattern);
      });

      it('edit button click should load edit Barrier page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Barrier');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', barrierPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Barrier', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('barrier').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', barrierPageUrlPattern);

        barrier = undefined;
      });
    });
  });

  describe('new Barrier page', () => {
    beforeEach(() => {
      cy.visit(`${barrierPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Barrier');
    });

    it.skip('should create an instance of Barrier', () => {
      cy.get(`[data-cy="name"]`).type('Rufiyaa').should('have.value', 'Rufiyaa');

      cy.get(`[data-cy="type"]`).select('EXIT');

      cy.get(`[data-cy="status"]`).select('DISABLED');

      cy.get(`[data-cy="parkingLot"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        barrier = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', barrierPageUrlPattern);
    });
  });
});
