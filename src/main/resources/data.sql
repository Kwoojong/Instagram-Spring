create table if not exists refresh_token
(
    token_value varchar(255) primary key,
    expired_at  timestamp not null,
    user_id   bigint    not null
);