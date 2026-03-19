
SELECT name 
FROM sys.foreign_keys 
WHERE parent_object_id = OBJECT_ID('stock_logs');


alter table stock_logs
drop column book_id


ALTER TABLE stock_logs
DROP column change_qty;

exec sp_rename 'stock_logs.cost_price','total_amount','column'



select * from stock_logs

create table log_item(
log_item_id int identity(1,1) not null primary key,
log_id int not null,
book_id int not null,
change_qty int not null,
cost_price decimal(10,2) not null


CONSTRAINT FK_logItem_stockLogs
FOREIGN KEY (log_id) REFERENCES stock_logs(log_id) ON DELETE CASCADE )

 alter table stock_logs
 add stock_type tinyint not null

 select * from stock_logs


 ALTER TABLE orders
ADD DELIVERY_METHOD nvarchar(50),
SHIPPING_FEE decimal(18, 2),
FINAL_AMOUNT decimal(18, 2);

select * from books

select * from users