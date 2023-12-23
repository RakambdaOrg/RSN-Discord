create table simkl
(
    id                 bigint not null,
    access_token       text,
    last_activity_date datetime,
    primary key (id)
) engine = InnoDB;
