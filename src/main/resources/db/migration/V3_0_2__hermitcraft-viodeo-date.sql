alter table hermitcraft_video
    add column if not exists notification_date datetime not null default current_timestamp();
