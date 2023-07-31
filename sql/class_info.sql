create table class_info
(
    id          bigint           not null
        primary key,
    name        varchar(32)      not null,
    total       int              not null,
    description text             null,
    deleted     int(1) default 0 not null,
    create_time datetime(6)      not null,
    update_time datetime(6)      not null,
    create_user bigint           not null,
    update_user bigint           not null
);

