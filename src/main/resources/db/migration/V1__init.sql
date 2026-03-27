create table if not exists users (
  id bigserial primary key,
  name varchar(120) not null,
  email varchar(180) unique not null,
  password_hash varchar(255) not null,
  role varchar(30) not null default 'CUSTOMER',
  created_at timestamptz not null default now()
);

create table if not exists categories (
  id bigserial primary key,
  name varchar(120) unique not null
);

create table if not exists products (
  id bigserial primary key,
  title varchar(200) not null,
  description text,
  price_inr integer not null,
  stock integer not null default 0,
  category_id bigint references categories(id),
  created_at timestamptz not null default now(),
  is_active boolean not null default true,
  is_deleted boolean not null default false
);

create table if not exists product_images (
  id bigserial primary key,
  product_id bigint not null references products(id) on delete cascade,
  image_url varchar(500) not null
);

create table if not exists orders (
  id bigserial primary key,
  user_id bigint not null references users(id),
  status varchar(30) not null default 'CREATED',
  total_inr integer not null,
  created_at timestamptz not null default now()
);

create table if not exists order_items (
  id bigserial primary key,
  order_id bigint not null references orders(id) on delete cascade,
  product_id bigint not null references products(id),
  qty integer not null,
  price_inr integer not null
);