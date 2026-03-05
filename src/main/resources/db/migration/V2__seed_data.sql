-- Insert Admin (password = Admin@123)
-- bcrypt hash for "Admin@123"
insert into users (name, email, password_hash, role)
values (
  'Admin',
  'admin@trendz.local',
  '$2a$10$Dow1B3wz6YQx2dQb3xYkGuR7Rk1ZgQYqH2kB7gZV5e5u6j0gQFv7K',
  'ADMIN'
)
on conflict (email) do nothing;

-- Categories
insert into categories (name)
values ('Handbags'), ('Sling Bags'), ('Tote Bags')
on conflict (name) do nothing;

-- Sample Products
insert into products (title, description, price_inr, stock, category_id, images)
values
('Trendz Firenze Classic Tote', 'Premium leather tote bag', 1999, 20,
 (select id from categories where name='Tote Bags'),
 '["/images/tote1.jpg"]'::jsonb),

('Trendz Firenze Sling Mini', 'Compact sling bag', 999, 40,
 (select id from categories where name='Sling Bags'),
 '["/images/sling1.jpg"]'::jsonb);