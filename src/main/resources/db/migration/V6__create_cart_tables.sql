create table if not exists carts (
  id bigserial primary key,
  user_id bigint not null unique references users(id) on delete cascade,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists cart_items (
  id bigserial primary key,
  cart_id bigint not null references carts(id) on delete cascade,
  product_id bigint not null references products(id),
  quantity integer not null,
  unit_price_snapshot numeric(12,2) not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);