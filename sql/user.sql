create table user
(
    id          bigint           not null
        primary key,
    username    varchar(32)      not null,
    password    varchar(64)      not null,
    name        varchar(20)      null,
    email       varchar(100)     null,
    gender      varchar(2)       null,
    age         int(3)           null,
    phone       varchar(11)      null,
    id_number   varchar(18)      null,
    user_grant  int(1)           not null comment '账号的权限等级
',
    avatar      varchar(36)      null,
    status      int(255)         not null,
    deleted     int(1) default 0 not null,
    create_time datetime(6)      not null,
    update_time datetime(6)      not null,
    create_user bigint           not null,
    update_user bigint           not null
)
    engine = InnoDB;

