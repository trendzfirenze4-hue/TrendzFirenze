
alter table orders
add column if not exists order_number varchar(50),
add column if not exists payment_method varchar(30),
add column if not exists payment_status varchar(30) default 'PENDING',
add column if not exists razorpay_order_id varchar(100),
add column if not exists razorpay_payment_id varchar(100),
add column if not exists razorpay_signature varchar(255);

create unique index if not exists ux_orders_order_number on orders(order_number);