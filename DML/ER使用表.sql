

CREATE TABLE users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    user_name NVARCHAR(50) NOT NULL,
    user_pwd VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    gender CHAR(1),
    birth DATE,
    phone_num VARCHAR(20),
    address NVARCHAR(255),
    img NVARCHAR(255),
    points INT DEFAULT 0,
    user_type TINYINT DEFAULT 0 NOT NULL, -- 0:Member, 1:Admin
    status TINYINT DEFAULT 1,             -- 1:Active, 0:Inactive
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME
);


CREATE TABLE books (
    book_id INT IDENTITY(1,1) PRIMARY KEY,
    book_name NVARCHAR(100) NOT NULL,
    author NVARCHAR(50) NOT NULL,
    translator NVARCHAR(50),
    press NVARCHAR(50) NOT NULL,   
    price DECIMAL(10, 2) NOT NULL,
    isbn VARCHAR(20) NOT NULL,
    stock INT DEFAULT 0 NOT NULL,
    short_desc NVARCHAR(300),     
    created_at DATETIME DEFAULT GETDATE(),
    on_shelf TINYINT DEFAULT 1 NOT NULL 
);


CREATE TABLE genres (
    genre_id INT IDENTITY(1,1) PRIMARY KEY,
    genre_name NVARCHAR(50) NOT NULL
);


CREATE TABLE book_genre_map (
    map_id INT IDENTITY(1,1) PRIMARY KEY,
    book_id INT NOT NULL,
    genre_id INT NOT NULL,
    CONSTRAINT FK_Map_Book FOREIGN KEY (book_id) REFERENCES books(book_id),
    CONSTRAINT FK_Map_Genre FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
);


CREATE TABLE book_images (
    image_id INT IDENTITY(1,1) PRIMARY KEY,
    book_id INT NOT NULL,
    image_url NVARCHAR(255) NOT NULL,
    CONSTRAINT FK_Img_Book FOREIGN KEY (book_id) REFERENCES books(book_id)
);


CREATE TABLE stock_logs (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    book_id INT NOT NULL,
    change_qty INT NOT NULL,           
    cost_price DECIMAL(10, 2),         
    supplier_name NVARCHAR(50),       
    log_time DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Log_Book FOREIGN KEY (book_id) REFERENCES books(book_id)
);


CREATE TABLE book_clubs (
    club_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,              -- 發起人
    book_id INT NOT NULL,              -- 討論書籍
    club_name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),         -- 企劃詳情 (MSSQL 用 NVARCHAR(MAX) 存大文本)
    event_date DATETIME NOT NULL,      -- 活動時間
    location NVARCHAR(100) NOT NULL,
    fee DECIMAL(10, 2) DEFAULT 0,      -- 現場費用
    max_participants INT NOT NULL,     -- 人數上限
    category NVARCHAR(50),             -- 活動分類 (商業/文學...)
    status TINYINT DEFAULT 0 NOT NULL, -- 0:待審, 1:通過, 2:駁回, 3:結束
    ai_evaluation NVARCHAR(MAX),       -- AI 評語
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Club_Host FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT FK_Club_Book FOREIGN KEY (book_id) REFERENCES books(book_id)
);


CREATE TABLE club_registrations (
    reg_id INT IDENTITY(1,1) PRIMARY KEY,
    club_id INT NOT NULL,
    user_id INT NOT NULL,
    status TINYINT DEFAULT 1 NOT NULL, 
    registered_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Reg_Club FOREIGN KEY (club_id) REFERENCES book_clubs(club_id),
    CONSTRAINT FK_Reg_User FOREIGN KEY (user_id) REFERENCES users(user_id)
);


CREATE TABLE cart (
    cart_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT DEFAULT 1 NOT NULL,
    created_time DATETIME DEFAULT GETDATE(),
    updated_time DATETIME,
    CONSTRAINT FK_Cart_User FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT FK_Cart_Book FOREIGN KEY (book_id) REFERENCES books(book_id)
);


CREATE TABLE orders (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(20),
    order_status VARCHAR(30) DEFAULT 'Pending', 
    recipient_at NVARCHAR(100),
    phone VARCHAR(20),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Order_User FOREIGN KEY (user_id) REFERENCES users(user_id)
);


CREATE TABLE order_item (
    item_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    book_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL, 
    CONSTRAINT FK_Item_Order FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT FK_Item_Book FOREIGN KEY (book_id) REFERENCES books(book_id)
);


CREATE TABLE reviews (
    review_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    rating TINYINT NOT NULL, 
    comment NVARCHAR(255),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Review_User FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT FK_Review_Book FOREIGN KEY (book_id) REFERENCES books(book_id)
);