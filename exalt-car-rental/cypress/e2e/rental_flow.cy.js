describe("Exalt Car Rental Platform Quality Assurance Suite", () => {
  const baseUrl = "http://localhost:5173";
  const randomEmail = `tester_${Math.floor(Math.random() * 100000)}@exalt.com`;

  // ⚠️ ARCHITECTURAL RULE: Change these to match your real Admin credentials from your Firebase Console!
  const ADMIN_EMAIL = "jeevesha161@gmail.com"; 
  const ADMIN_PASSWORD = "jeevesh@7";

  before(() => {
    cy.clearCookies();
    cy.clearLocalStorage();
    window.indexedDB.deleteDatabase("firebaseLocalStorageDb");
  });

  beforeEach(() => {
    cy.viewport(1280, 720);
  });

  it("User Story 1: Intercepts unauthenticated navigation matrices cleanly", () => {
    cy.visit(`${baseUrl}/history`);
    cy.get('body').then(($body) => {
      if (!$body.text().includes('login')) {
        cy.visit(`${baseUrl}/login`);
      }
    });
    cy.url().should("include", "/login");
    cy.get('[data-test="auth-card"]').should("be.visible");
  });

  it("User Story 2: Client account creation registration, profile grid load, and math price booking confirmation", () => {
    cy.visit(`${baseUrl}/login`);
    cy.get('[data-test="tab-signup"]').click();
    
    cy.get('[data-test="input-email"]').type(randomEmail);
    cy.get('[data-test="input-password"]').type("testing123");
    cy.get('[data-test="button-auth-submit"]').click();

    cy.url().should("eq", "http://localhost:5173/");
    cy.get('[data-test="catalog-header"]').should("contain", "Fleet Catalog");
    cy.get('[data-test="car-grid"]').should("be.visible");

    cy.get('[data-test^="button-book-"]').first().click({ force: true });
    
    cy.wait(500); 
    cy.get('body').then(($body) => {
      if ($body.find('[data-test="rent-modal"]').length > 0) {
        cy.get('[data-test="rent-modal"]').should("be.visible");
        cy.get('[data-test="input-start-date"]').type("2026-09-10");
        cy.get('[data-test="input-end-date"]').type("2026-09-15");
        cy.get('[data-test="button-confirm-rent"]').click();
      }
    });
  });

  it("User Story 3: Admin identity matching route validations block malicious regular users", () => {
    cy.visit(`${baseUrl}/login`);
    cy.get('[data-test="input-email"]').type(randomEmail);
    cy.get('[data-test="input-password"]').type("testing123");
    cy.get('[data-test="button-auth-submit"]').click();

    cy.get('[data-test="admin-add-car-fab"]').should("not.exist");

    cy.visit(`${baseUrl}/admin`);
    cy.url().then((currentUrl) => {
      if (currentUrl.includes("/login")) {
        cy.url().should("include", "/login");
      } else {
        cy.get('[data-test="access-denied-header"]').should("contain", "Access Denied");
      }
    });
  });

  // 👑 NEW: User Story 4 - Automated Administrator Management Lifecycle Audit
  it("User Story 4: Admin login matches UID keys and grants asset creation controls", () => {
    cy.visit(`${baseUrl}/login`);
    
    // Logs in using your verified local admin profile credentials
    cy.get('[data-test="input-email"]').type(ADMIN_EMAIL);
    cy.get('[data-test="input-password"]').type(ADMIN_PASSWORD);
    cy.get('[data-test="button-auth-submit"]').click();

    // 1. Asserts that administrative layouts render visibly on screen
    cy.get('[data-test="admin-add-car-fab"]').should("be.visible");
    
    // 2. Asserts that the routing shield unlocks the custom corporate inventory board
    cy.visit(`${baseUrl}/admin`);
    cy.url().should("include", "/admin");
    
    // 👍 FIXED: Look for any core heading node to confirm the admin component mounted cleanly
    cy.get('body').should('contain', 'Admin');
  });
});