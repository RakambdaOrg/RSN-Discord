alter table anilist
    add column if not exists enabled boolean not null default true;
