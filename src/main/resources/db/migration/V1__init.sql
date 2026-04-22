-- =========================================
-- V1__init.sql (FINAL CORRECTED)
-- =========================================

create table if not exists users (
  id bigserial primary key,
  name varchar(120) not null,
  email varchar(180) unique not null,
  password_hash varchar(255) not null,
  role varchar(30) not null default 'CUSTOMER',
  phone varchar(20),
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
  mrp_inr integer,
  stock integer not null default 0,
  category_id bigint references categories(id),
  created_at timestamptz not null default now(),
  is_active boolean not null default true,
  is_deleted boolean not null default false
);

create table if not exists product_images (
  id bigserial primary key,
  product_id bigint not null references products(id) on delete cascade,
  image_url varchar(500) not null,
  cloudinary_public_id varchar(255)
);

create table if not exists addresses (
  id bigserial primary key,
  user_id bigint not null references users(id) on delete cascade,
  full_name varchar(120) not null,
  phone varchar(20) not null,
  line1 varchar(255) not null,
  line2 varchar(255),
  city varchar(120) not null,
  state varchar(120) not null,
  pincode varchar(20) not null,
  country varchar(80) not null default 'India',
  is_default boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists gift_boxes (
  id bigserial primary key,
  name varchar(120) not null,
  description text,
  price_inr integer not null,
  image_path varchar(500),
  cloudinary_public_id varchar(255),
  stock integer not null default 0,
  is_active boolean not null default true,
  is_deleted boolean not null default false,
  created_at timestamptz not null default current_timestamp,
  updated_at timestamptz
);

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

create table if not exists orders (
  id bigserial primary key,
  order_number varchar(50) not null unique,
  user_id bigint not null references users(id) on delete cascade,
  payment_method varchar(30) not null,
  payment_status varchar(30) not null default 'PENDING',
  status varchar(30) not null,
  razorpay_order_id varchar(100),
  razorpay_payment_id varchar(100),
  razorpay_signature varchar(255),
  subtotal_amount numeric(12,2) not null,
  shipping_amount numeric(12,2) not null,
  discount_amount numeric(12,2) not null default 0,
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
  created_at timestamptz not null default now()
);

create table if not exists order_items (
  id bigserial primary key,
  order_id bigint not null references orders(id) on delete cascade,
  product_id bigint not null references products(id),
  product_title varchar(200) not null,
  image_url varchar(500),
  quantity integer not null,
  unit_price numeric(12,2) not null,
  line_total numeric(12,2) not null
);

create table if not exists product_reviews (
  id bigserial primary key,
  product_id bigint not null references products(id) on delete cascade,
  reviewer_name varchar(120) not null,
  rating integer not null check (rating >= 1 and rating <= 5),
  review_text text not null,
  is_featured boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists coupons (
  id bigserial primary key,
  code varchar(50) unique not null,
  description varchar(255),
  discount_type varchar(20) not null,
  discount_value numeric(12,2) not null,
  min_order_value numeric(12,2),
  max_discount numeric(12,2),
  usage_limit int,
  used_count int not null default 0,
  active boolean not null default true,
  starts_at timestamptz,
  ends_at timestamptz,
  created_at timestamptz not null default now()
);

create table if not exists coupon_usages (
  id bigserial primary key,
  coupon_id bigint not null references coupons(id),
  user_id bigint not null references users(id),
  order_id bigint references orders(id),
  used_at timestamptz not null default now()
);

create table if not exists brand_showcases (
  id bigserial primary key,
  title varchar(200) not null,
  subtitle varchar(500),
  model_image_url varchar(500) not null,
  cloudinary_public_id varchar(255),
  display_order integer not null default 0,
  is_active boolean not null default true,
  is_deleted boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz
);

create table if not exists brand_showcase_items (
  id bigserial primary key,
  brand_showcase_id bigint not null references brand_showcases(id),
  product_id bigint not null references products(id),
  display_order integer not null default 0
);

create table if not exists hero_sections (
  id bigserial primary key,
  title varchar(200) not null,
  description text,
  image_url varchar(500) not null,
  cloudinary_public_id varchar(255),
  product_id bigint not null references products(id),
  sort_order integer not null default 0,
  is_active boolean not null default true,
  is_deleted boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists gift_set_carts (
  id bigserial primary key,
  user_id bigint not null unique references users(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists gift_set_cart_items (
  id bigserial primary key,
  gift_set_cart_id bigint not null references gift_set_carts(id) on delete cascade,
  product_id bigint not null references products(id),
  gift_box_id bigint not null references gift_boxes(id),
  product_price_snapshot integer not null,
  gift_box_price_snapshot integer not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint uk_gift_set_cart_product unique (gift_set_cart_id, product_id)
);

create table if not exists gift_set_orders (
  id bigserial primary key,
  order_number varchar(50) not null unique,
  user_id bigint not null references users(id),
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
  created_at timestamptz not null default now()
);

create table if not exists gift_set_order_items (
  id bigserial primary key,
  gift_set_order_id bigint not null references gift_set_orders(id) on delete cascade,
  product_id bigint not null references products(id),
  product_title varchar(200) not null,
  product_image_url varchar(500),
  product_price_snapshot numeric(12,2) not null,
  gift_box_id bigint not null references gift_boxes(id),
  gift_box_name varchar(200) not null,
  gift_box_image_url varchar(500),
  gift_box_price_snapshot numeric(12,2) not null,
  line_total numeric(12,2) not null,
  created_at timestamptz not null default now()
);

create table if not exists bulk_order_inquiries (
  id bigserial primary key,
  product_id bigint not null references products(id),
  customer_name varchar(120) not null,
  email varchar(160) not null,
  phone varchar(30) not null,
  company_name varchar(160),
  quantity integer not null,
  message text,
  status varchar(30) not null default 'NEW',
  created_at timestamptz not null default now()
);

create table if not exists instagram_auth (
  id bigserial primary key,
  instagram_user_id varchar(100) not null,
  access_token text not null,
  expires_at timestamp not null,
  refreshed_at timestamp,
  created_at timestamp not null default now(),
  updated_at timestamp not null default now(),
  is_active boolean not null default true
);

create table if not exists instagram_media_cache (
  id bigserial primary key,
  cache_key varchar(100) not null unique,
  payload_json text not null,
  updated_at timestamp not null
);

create index if not exists idx_brand_showcases_active_deleted_order
  on brand_showcases (is_active, is_deleted, display_order, id);

create index if not exists idx_brand_showcase_items_showcase_order
  on brand_showcase_items (brand_showcase_id, display_order, id);

create index if not exists idx_hero_sections_active_deleted_sort
  on hero_sections (is_active, is_deleted, sort_order);

create index if not exists idx_gift_set_cart_items_cart_id
  on gift_set_cart_items (gift_set_cart_id);

create index if not exists idx_gift_set_cart_items_product_id
  on gift_set_cart_items (product_id);

create index if not exists idx_gift_set_cart_items_gift_box_id
  on gift_set_cart_items (gift_box_id);

create index if not exists idx_gift_set_orders_user_id
  on gift_set_orders (user_id);

create index if not exists idx_gift_set_order_items_order_id
  on gift_set_order_items (gift_set_order_id);

create index if not exists idx_bulk_order_inquiries_created_at
  on bulk_order_inquiries (created_at desc);

create index if not exists idx_bulk_order_inquiries_product_id
  on bulk_order_inquiries (product_id);

create index if not exists idx_instagram_auth_active
  on instagram_auth (is_active);