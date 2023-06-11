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

describe('Reservation e2e test', () => {
  const reservationPageUrl = '/reservation';
  const reservationPageUrlPattern = new RegExp('/reservation(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const reservationSample = {
    parkingSpotId: 'c626e37e-74e9-472c-8a24-b61012b52334',
    startTime: '2023-06-10T16:02:46.110Z',
    endTime: '2023-06-10T12:43:21.177Z',
    status: 'ACTIVE',
    reservationCode: 'BK-3{10, 14}',
  };

  let reservation;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reservations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reservations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reservations/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (reservation) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/reservations/${reservation.id}`,
      }).then(() => {
        reservation = undefined;
      });
    }
  });

  it('Reservations menu should load Reservations page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('reservation');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Reservation').should('exist');
    cy.url().should('match', reservationPageUrlPattern);
  });

  describe('Reservation page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(reservationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Reservation page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/reservation/new$'));
        cy.getEntityCreateUpdateHeading('Reservation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/reservations',
          body: reservationSample,
        }).then(({ body }) => {
          reservation = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/reservations+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [reservation],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(reservationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Reservation page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('reservation');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });

      it('edit button click should load edit Reservation page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reservation');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });

      it('edit button click should load edit Reservation page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Reservation');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);
      });

      it('last delete button click should delete instance of Reservation', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('reservation').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', reservationPageUrlPattern);

        reservation = undefined;
      });
    });
  });

  describe('new Reservation page', () => {
    beforeEach(() => {
      cy.visit(`${reservationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Reservation');
    });

    it('should create an instance of Reservation', () => {
      cy.get(`[data-cy="parkingSpotId"]`)
        .type('c1be4527-67da-425d-8c43-6030b6c98ae5')
        .invoke('val')
        .should('match', new RegExp('c1be4527-67da-425d-8c43-6030b6c98ae5'));

      cy.get(`[data-cy="startTime"]`).type('2023-06-10T07:48').blur().should('have.value', '2023-06-10T07:48');

      cy.get(`[data-cy="endTime"]`).type('2023-06-10T10:46').blur().should('have.value', '2023-06-10T10:46');

      cy.get(`[data-cy="status"]`).select('CANCELLED');

      cy.get(`[data-cy="reservationCode"]`).type('ZP-0{10, 14}').should('have.value', 'ZP-0{10, 14}');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        reservation = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', reservationPageUrlPattern);
    });
  });
});
