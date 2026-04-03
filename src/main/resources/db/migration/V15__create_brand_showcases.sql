create table brand_showcases (
    id bigserial primary key,
    title varchar(200) not null,
    subtitle varchar(500),
    model_image_url varchar(500) not null,
    cloudinary_public_id varchar(255),
    display_order integer not null default 0,
    is_active boolean not null default true,
    is_deleted boolean not null default false,
    created_at timestamp with time zone not null default now(),
    updated_at timestamp with time zone
);

create table brand_showcase_items (
    id bigserial primary key,
    brand_showcase_id bigint not null,
    product_id bigint not null,
    display_order integer not null default 0,
    constraint fk_brand_showcase_items_showcase
        foreign key (brand_showcase_id) references brand_showcases(id),
    constraint fk_brand_showcase_items_product
        foreign key (product_id) references products(id)
);

create index idx_brand_showcases_active_deleted_order
    on brand_showcases (is_active, is_deleted, display_order, id);

create index idx_brand_showcase_items_showcase_order
    on brand_showcase_items (brand_showcase_id, display_order, id);