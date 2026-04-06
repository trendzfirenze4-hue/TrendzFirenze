create table if not exists gift_set_carts (
    id bigserial primary key,
    user_id bigint not null unique,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint fk_gift_set_carts_user
        foreign key (user_id) references users(id)
);

create table if not exists gift_set_cart_items (
    id bigserial primary key,
    gift_set_cart_id bigint not null,
    product_id bigint not null,
    gift_box_id bigint not null,
    product_price_snapshot integer not null,
    gift_box_price_snapshot integer not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    constraint fk_gift_set_cart_items_cart
        foreign key (gift_set_cart_id) references gift_set_carts(id) on delete cascade,
    constraint fk_gift_set_cart_items_product
        foreign key (product_id) references products(id),
    constraint fk_gift_set_cart_items_gift_box
        foreign key (gift_box_id) references gift_boxes(id),
    constraint uk_gift_set_cart_product unique (gift_set_cart_id, product_id)
);

create index if not exists idx_gift_set_cart_items_cart_id
    on gift_set_cart_items(gift_set_cart_id);

create index if not exists idx_gift_set_cart_items_product_id
    on gift_set_cart_items(product_id);

create index if not exists idx_gift_set_cart_items_gift_box_id
    on gift_set_cart_items(gift_box_id);