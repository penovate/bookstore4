// Custom command to log steps in the Cypress Command Log
Cypress.Commands.add('step', (currentStep, nextStep) => {
    Cypress.log({
        name: 'STEP',
        displayName: `STEP ${currentStep}`,
        message: nextStep,
        consoleProps: () => {
            return {
                'Current Step': currentStep,
                'Next Action': nextStep,
            }
        },
    })
})

// Custom command to simulate login by setting token directly or going through UI
// For demo purposes, we'll go through UI to show the process
Cypress.Commands.add('login', (email, password) => {
    cy.session([email, password], () => {
        cy.visit('/login')
        cy.get('input[type="text"]').type(email)
        cy.get('input[type="password"]').type(password)
        cy.contains('button', '登入系統').click()
        // Wait for login to complete (e.g., redirect to home or check for avatar)
        cy.url().should('not.include', '/login')
    }, {
        cacheAcrossSpecs: true
    })
})

// Utility to handle SweetAlert2 confirmations
Cypress.Commands.add('confirmSwal', () => {
    cy.get('.swal2-confirm').click()
})
