create table if not exists product_images (
  id bigserial primary key,
  product_id bigint not null references products(id) on delete cascade,
  image_url varchar(500) not null
);

insert into product_images (product_id, image_url)
select
  p.id,
  img.value
from products p,
jsonb_array_elements_text(p.images) as img(value);