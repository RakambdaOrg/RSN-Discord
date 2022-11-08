create table archive
(
    id       bigint not null,
    tag_id   bigint not null,
    guild_id bigint not null,
    primary key (Id),
    foreign key fk_archive_Guild (guild_id) references guild (id) on update cascade on delete cascade
) engine = InnoDB;