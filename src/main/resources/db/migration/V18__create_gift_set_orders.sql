create table if not exists gift_set_orders (
    id bigserial primary key,
    order_number varchar(50) not null unique,
    user_id bigint not null,
    payment_method varchar(30) not null,
    payment_status varchar(30) not null,
    status varchar(30) not null,
    razorpay_order_id varchar(100),
    razorpay_payment_id varchar(100),
    razorpay_signature varchar(255),
    subtotal_amount numeric(12,2) not null,
    shipping_amount numeric(12,2) not null,
    discount_amount numeric(12,2) not null,
    total_amount numeric(12,2) not null,
    coupon_code varchar(50),
    address_full_name varchar(120) not null,
    address_phone varchar(20) not null,
    address_line1 varchar(255) not null,
    address_line2 varchar(255),
    address_city varchar(120) not null,
    address_state varchar(120) not null,
    address_pincode varchar(20) not null,
    address_country varchar(80) not null,
    created_at timestamptz not null,
    constraint fk_gift_set_orders_user
        foreign key (user_id) references users(id)
);

create table if not exists gift_set_order_items (
    id bigserial primary key,
    gift_set_order_id bigint not null,
    product_id bigint not null,
    product_title varchar(200) not null,
    product_image_url varchar(500),
    product_price_snapshot numeric(12,2) not null,
    gift_box_id bigint not null,
    gift_box_name varchar(200) not null,
    gift_box_image_url varchar(500),
    gift_box_price_snapshot numeric(12,2) not null,
    line_total numeric(12,2) not null,
    created_at timestamptz not null,
    constraint fk_gift_set_order_items_order
        foreign key (gift_set_order_id) references gift_set_orders(id) on delete cascade,
    constraint fk_gift_set_order_items_product
        foreign key (product_id) references products(id),
    constraint fk_gift_set_order_items_gift_box
        foreign key (gift_box_id) references gift_boxes(id)
);

create index if not exists idx_gift_set_orders_user_id
    on gift_set_orders(user_id);

create index if not exists idx_gift_set_order_items_order_id
    on gift_set_order_items(gift_set_order_id);