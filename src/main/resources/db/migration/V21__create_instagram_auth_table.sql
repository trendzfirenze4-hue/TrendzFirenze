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

create index if not exists idx_instagram_auth_active on instagram_auth(is_active);