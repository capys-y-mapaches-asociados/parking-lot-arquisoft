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
    customerId: '585a06a7-0367-442a-84cf-1c6666e09dc2',
    parkingSpotId: 'ec995415-c650-4e86-bc5e-80eee7072bcd',
    startTime: '2023-06-10T20:36:12.416Z',
    endTime: '2023-06-10T22:32:15.734Z',
    status: 'ACTIVE',
    reservationCode: 'GF-d{10, 14}',
  };

  let reservation;
  let customer;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/customers',
      body: {
        firstName: 'Marques',
        lastName: 'Pollich',
        email: 'dyC6q-@Z.a.Vs.pq2.aWXc_u.U.cjz',
        password: '5fe2C7C90A732Bc1AfaFD7a03724a3d02D086BdD1BceD18Be2F292F8bd8CF30C',
      },
    }).then(({ body }) => {
      customer = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/reservations+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/reservations').as('postEntityRequest');
    cy.intercept('DELETE', '/api/reservations/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/customers', {
      statusCode: 200,
      body: [customer],
    });

    cy.intercept('GET', '/api/notifications', {
      statusCode: 200,
      body: [],
    });
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

  afterEach(() => {
    if (customer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/customers/${customer.id}`,
      }).then(() => {
        customer = undefined;
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
          body: {
            ...reservationSample,
            customerId: customer,
          },
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
      cy.get(`[data-cy="customerId"]`)
        .type('c1be4527-67da-425d-8c43-6030b6c98ae5')
        .invoke('val')
        .should('match', new RegExp('c1be4527-67da-425d-8c43-6030b6c98ae5'));

      cy.get(`[data-cy="parkingSpotId"]`)
        .type('b9c8f98c-626e-437e-b4e9-72cca24b6101')
        .invoke('val')
        .should('match', new RegExp('b9c8f98c-626e-437e-b4e9-72cca24b6101'));

      cy.get(`[data-cy="startTime"]`).type('2023-06-10T21:40').blur().should('have.value', '2023-06-10T21:40');

      cy.get(`[data-cy="endTime"]`).type('2023-06-10T07:05').blur().should('have.value', '2023-06-10T07:05');

      cy.get(`[data-cy="status"]`).select('ACTIVE');

      cy.get(`[data-cy="reservationCode"]`).type('FF-a{10, 14}').should('have.value', 'FF-a{10, 14}');

      cy.get(`[data-cy="customerId"]`).select(1);

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
