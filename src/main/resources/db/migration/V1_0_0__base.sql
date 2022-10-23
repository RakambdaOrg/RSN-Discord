create table guild
(
    id     bigint  not null,
    volume integer not null,
    primary key (id)
) engine = InnoDB;

create table anilist
(
    id                     bigint not null,
    access_token           text,
    last_activity_date     datetime,
    last_media_list_date   datetime,
    last_notification_date datetime,
    refresh_token          text,
    refresh_token_expire   datetime,
    user_id                integer,
    primary key (id)
) engine = InnoDB;

create table channel
(
    id         integer      not null auto_increment,
    channel_id bigint       not null,
    type       varchar(255) not null,
    guild_id   bigint       not null,
    primary key (Id),
    unique index idx_channel_GuildTypeChannel (guild_id, type, channel_id),
    foreign key fk_channel_Guild (guild_id) references guild (id)
) engine = InnoDB;

create table hermitcraft_video
(
    id varchar(32) not null,
    primary key (id)
) engine = InnoDB;

create table rss
(
    id                    integer      not null auto_increment,
    last_publication_date datetime,
    title                 varchar(255),
    url                   varchar(255) not null,
    guild_id              bigint       not null,
    primary key (Id),
    unique index idx_rss_GuildUrl (guild_id, url),
    foreign key fk_rss_Guild (guild_id) references guild (id)
) engine = InnoDB;

create table trakt
(
    id                   bigint not null,
    access_token         text,
    last_activity_date   datetime,
    refresh_token        text,
    refresh_token_expire datetime,
    username             varchar(32),
    primary key (id)
) engine = InnoDB;

create table twitter
(
    id            integer      not null auto_increment,
    last_tweet_id varchar(255),
    search        varchar(255) not null,
    type          varchar(255) not null,
    guild_id      bigint       not null,
    primary key (id),
    unique index idx_twitter_GuildTypeQuery (guild_id, type, search),
    foreign key fk_twitter_Guild (guild_id) references guild (id)
) engine = InnoDB;
