describe('Admin Book & Inventory Management Flow', () => {
  const adminUser = {
    email: 'onlinebookstoreforjava@gmail.com',
    password: '123456',
  }

  it('Executes the Admin Flow: Login -> Add Book -> Add Stock -> Frontend', () => {
    // 1. Login
    cy.visit('/dev/user/login') // Relative path, assuming baseUrl is localhost:5173

    cy.get('input[type="email"]').clear().type(adminUser.email)
    cy.get('input[type="password"]').clear().type(adminUser.password)
    cy.contains('button', '登入').click()

    // 2. Navigate to Backend System -> Book Management
    // Assuming "後台系統" is accessible or we are redirected.
    // If redirected to /dev/admin/..., we look for sidebar.
    // Wait for url to change or sidebar to appear
    cy.visit('/home')

    // Click "書籍管理"
    cy.get('.v-list-item-title').contains('書籍管理').click()

    // 3. Click "新增書籍"
    cy.get('.v-list-item-title').contains('新增書籍').click()

    // 4. Upload Cover Image
    // Locate the file input. Vuetify file inputs might be hidden, need to target the input type=file
    // '巴菲特寫給股東的信.png' should be in cypress/fixtures
    cy.get('input[type="file"]').selectFile('cypress/fixtures/巴菲特寫給股東的信.png', {
      force: true,
    })

    // Fill other required fields if strictly necessary (User didn't specify, but "Confirm Add" implies validity).
    // Assuming there is "One-click Input" for book details too, as per previous test pattern, or we just upload.
    // If validation fails, we might need to fill more.
    // The user's step 8 mentions "One-click" for Purchase Order, but step 3-5 doesn't explicitly say "One-click" for Book.
    // HOWEVER, previous test used it. I'll check if "一鍵輸入" exists for books and click it to ensure success.
    cy.get('button').contains('一鍵輸入').click() // Safely try to fill data

    // 5. Confirm Add
    cy.contains('button', '新增書籍').click()
    // Handle SweetAlert2 validation/confirmation if any
    cy.get('.swal2-confirm').click()

    // 6. Click "進退貨管理"
    cy.get('.v-list-item-title').contains('進退貨管理').click()

    // 7. Click "新增進貨單"
    cy.contains('button', '新增進貨單').click() // Or sidebar item if it's there
    // If it's a submenu:
    // cy.get('.v-list-item-title').contains('新增進貨單').click();
    // Based on typical sidebar, it might be a button on the main View or a submenu.
    // Let's assume it's a button on the page or sidebar. I'll try containing text.

    // 8. Click "One-click Input" to fill data, then Submit
    cy.contains('button', '一鍵輸入').click()
    cy.contains('button', '確認送出').click() // Or similar text
    cy.get('.swal2-confirm').click()

    // 9. Click "Go to Frontend"
    // This might be a button in the app bar or sidebar
    cy.contains('前往前台').click() // Adjust selector if specific text differs "前往前台網頁"
  })
})
