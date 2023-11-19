alter table trakt
    add column if not exists enabled boolean not null default true;
