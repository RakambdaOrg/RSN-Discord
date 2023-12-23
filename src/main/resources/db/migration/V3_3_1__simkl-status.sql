alter table simkl
    add column if not exists enabled boolean not null default true;
