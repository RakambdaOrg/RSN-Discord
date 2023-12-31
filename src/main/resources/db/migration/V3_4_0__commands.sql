create table command
(
    id       int    not null auto_increment,
    guild_id bigint not null,
    name     varchar(255),
    enabled  boolean,
    primary key (id),
    unique index idx_command_GuildName (guild_id, name),
    foreign key fk_command_Guild (guild_id) references guild (id)
        on update cascade
        on delete cascade
) engine = InnoDB;
