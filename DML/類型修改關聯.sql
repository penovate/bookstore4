use bookStore

create table book_genre_map(
book_id int not null,
genre_id int not null,
primary key(book_id,genre_id),
foreign key(book_id) references books(book_id) on delete cascade,
foreign key(genre_id) references genres(genre_id) on delete cascade
);





delete from order_item
dbcc checkident('order_item',reseed,0)

delete from orders
dbcc checkident('orders',reseed,0)

delete from reviews 
dbcc checkident('reviews',reseed,0)

delete from books
dbcc checkident('books',reseed,0)

delete from genres
dbcc checkident('genres',reseed,0)

alter table books drop column genre_id

select * from books

select * from book_images


SELECT name 
FROM sys.foreign_keys 
WHERE parent_object_id = OBJECT_ID('books');

ALTER TABLE books DROP CONSTRAINT FK__books__genre_id__5FB337D6;


alter table books drop column genre_id

select *from books