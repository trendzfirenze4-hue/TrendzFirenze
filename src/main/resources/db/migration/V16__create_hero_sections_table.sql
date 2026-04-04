create table hero_sections (
    id bigserial primary key,
    title varchar(200) not null,
    description text,
    image_url varchar(500) not null,
    cloudinary_public_id varchar(255),
    product_id bigint not null,
    sort_order integer not null default 0,
    is_active boolean not null default true,
    is_deleted boolean not null default false,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now(),
    constraint fk_hero_sections_product
        foreign key (product_id) references products(id)
);

create index idx_hero_sections_active_deleted_sort
    on hero_sections(is_active, is_deleted, sort_order);