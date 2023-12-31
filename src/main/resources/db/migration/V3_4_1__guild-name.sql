alter table guild
    add column if not exists name varchar(32) not null default 'Unknown';
