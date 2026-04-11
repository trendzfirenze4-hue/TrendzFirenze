create table if not exists bulk_order_inquiries (
    id bigserial primary key,
    product_id bigint not null,
    customer_name varchar(120) not null,
    email varchar(160) not null,
    phone varchar(30) not null,
    company_name varchar(160),
    quantity integer not null,
    message text,
    status varchar(30) not null default 'NEW',
    created_at timestamptz not null default now(),
    constraint fk_bulk_order_product
        foreign key (product_id) references products(id)
);

create index if not exists idx_bulk_order_inquiries_created_at
    on bulk_order_inquiries(created_at desc);

create index if not exists idx_bulk_order_inquiries_product_id
    on bulk_order_inquiries(product_id);