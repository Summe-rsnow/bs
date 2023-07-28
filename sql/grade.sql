create table grade
(
    id          bigint           not null
        primary key,
    course_id   bigint(255)      not null,
    student_id  bigint           not null,
    teacher_id  bigint           not null,
    score       int(3)           not null,
    deleted     int(1) default 0 not null,
    create_time datetime(6)      not null,
    update_time datetime(6)      not null,
    create_user bigint           not null,
    update_user bigint           not null
)
    engine = InnoDB;

