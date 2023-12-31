create table command
(
    id      bigint not null,
    guild_id bigint not null,
    name    varchar(255),
    enabled boolean,
    primary key (id)
) engine = InnoDB;
