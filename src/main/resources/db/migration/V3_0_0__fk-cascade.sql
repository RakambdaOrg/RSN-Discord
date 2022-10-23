alter table audio
    drop foreign key if exists fk_audio_Guild;

alter table audio
    add foreign key fk_audio_Guild (guild_id) references guild (id)
        on update cascade
        on delete cascade;

alter table channel
    drop foreign key if exists fk_channel_Guild;

alter table channel
    add foreign key fk_channel_Guild (guild_id) references guild (id)
        on update cascade
        on delete cascade;

alter table rss
    drop foreign key if exists fk_rss_Guild;

alter table rss
    add foreign key fk_rss_Guild (guild_id) references guild (id)
        on update cascade
        on delete cascade;

alter table twitter
    drop foreign key if exists fk_twitter_Guild;

alter table twitter
    add foreign key fk_twitter_Guild (guild_id) references guild (id)
        on update cascade
        on delete cascade;