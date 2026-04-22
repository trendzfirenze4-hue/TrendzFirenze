-- =========================================
-- V2__seed_data.sql (FINAL CORRECTED)
-- =========================================

-- Admin
insert into users (name, email, password_hash, role)
values (
  'Admin',
  'admin@gmail.com',
  '$2a$10$NNWDLRTzRKUrkQzQjdsMUOSccRlsMcMl1y0uN6bZttf3xNt1VoEgG',
  'ADMIN'
)
on conflict do nothing;

-- Categories
insert into categories (name)
values ('Handbags'), ('Sling Bags'), ('Tote Bags')
on conflict do nothing;

-- Products
insert into products (
  title,
  description,
  price_inr,
  mrp_inr,
  stock,
  category_id
)
values
(
  'Trendz Firenze Classic Tote',
  'Premium leather tote bag',
  1999,
  2499,
  20,
  (select id from categories where name='Tote Bags')
),
(
  'Trendz Firenze Sling Mini',
  'Compact sling bag',
  999,
  1299,
  40,
  (select id from categories where name='Sling Bags')
);

-- Product Images (CORRECT WAY)
insert into product_images (product_id, image_url)
select id, '/images/tote1.jpg'
from products where title='Trendz Firenze Classic Tote';

insert into product_images (product_id, image_url)
select id, '/images/sling1.jpg'
from products where title='Trendz Firenze Sling Mini';