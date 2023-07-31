create table class_course
(
    id          bigint           not null
        primary key,
    class_id    bigint           not null,
    course_id  bigint           not null,
    deleted     int(1) default 0 not null,
    create_time datetime(6)      not null,
    update_time datetime(6)      not null,
    create_user bigint           not null,
    update_user bigint           not null
);

