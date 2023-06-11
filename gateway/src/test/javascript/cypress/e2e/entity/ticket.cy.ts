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

describe('Ticket e2e test', () => {
  const ticketPageUrl = '/ticket';
  const ticketPageUrlPattern = new RegExp('/ticket(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const ticketSample = {"ticketCode":"5{6, 10}","issuedAt":"2023-06-10T16:51:44.648Z","parkingSpotId":"9ca52139-acf3-4774-afce-308d42b658f5","entryTime":"2023-06-10T23:59:25.331Z","exitTime":"2023-06-10T09:15:42.890Z","status":"ACTIVE"};

  let ticket;
  // let parkingSpot;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/parking-spots',
      body: {"number":7194,"status":"RESERVED","spotType":"HANDICAPPED","spotVehicle":"E_CAR"},
    }).then(({ body }) => {
      parkingSpot = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/tickets+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tickets').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tickets/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/parking-spots', {
      statusCode: 200,
      body: [parkingSpot],
    });

  });
   */

  afterEach(() => {
    if (ticket) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tickets/${ticket.id}`,
      }).then(() => {
        ticket = undefined;
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
  });
   */

  it('Tickets menu should load Tickets page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('ticket');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Ticket').should('exist');
    cy.url().should('match', ticketPageUrlPattern);
  });

  describe('Ticket page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(ticketPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Ticket page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/ticket/new$'));
        cy.getEntityCreateUpdateHeading('Ticket');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tickets',
          body: {
            ...ticketSample,
            parkingSpotId: parkingSpot,
          },
        }).then(({ body }) => {
          ticket = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tickets+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [ticket],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(ticketPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(ticketPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Ticket page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('ticket');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });

      it('edit button click should load edit Ticket page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ticket');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });

      it('edit button click should load edit Ticket page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Ticket');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);
      });

      it.skip('last delete button click should delete instance of Ticket', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('ticket').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', ticketPageUrlPattern);

        ticket = undefined;
      });
    });
  });

  describe('new Ticket page', () => {
    beforeEach(() => {
      cy.visit(`${ticketPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Ticket');
    });

    it.skip('should create an instance of Ticket', () => {
      cy.get(`[data-cy="ticketCode"]`).type('7{6, 10}').should('have.value', '7{6, 10}');

      cy.get(`[data-cy="issuedAt"]`).type('2023-06-10T19:22').blur().should('have.value', '2023-06-10T19:22');

      cy.get(`[data-cy="parkingSpotId"]`)
        .type('d82ae84d-3fff-4c1d-9222-e1cb6d85ca6c')
        .invoke('val')
        .should('match', new RegExp('d82ae84d-3fff-4c1d-9222-e1cb6d85ca6c'));

      cy.get(`[data-cy="entryTime"]`).type('2023-06-10T19:53').blur().should('have.value', '2023-06-10T19:53');

      cy.get(`[data-cy="exitTime"]`).type('2023-06-10T19:02').blur().should('have.value', '2023-06-10T19:02');

      cy.get(`[data-cy="status"]`).select('ACTIVE');

      cy.get(`[data-cy="parkingSpotId"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(201);
        ticket = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response.statusCode).to.equal(200);
      });
      cy.url().should('match', ticketPageUrlPattern);
    });
  });
});
