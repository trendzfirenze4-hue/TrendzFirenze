CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_type VARCHAR(20) NOT NULL,
    discount_value NUMERIC(12,2) NOT NULL,
    min_order_value NUMERIC(12,2),
    max_discount NUMERIC(12,2),
    usage_limit INT,
    used_count INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    starts_at TIMESTAMP WITH TIME ZONE,
    ends_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE coupon_usages (
    id BIGSERIAL PRIMARY KEY,
    coupon_id BIGINT NOT NULL REFERENCES coupons(id),
    user_id BIGINT NOT NULL REFERENCES users(id),
    order_id BIGINT REFERENCES orders(id),
    used_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

ALTER TABLE orders
ADD COLUMN discount_amount NUMERIC(12,2) NOT NULL DEFAULT 0,
ADD COLUMN coupon_code VARCHAR(50);