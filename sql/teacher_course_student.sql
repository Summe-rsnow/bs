create table teacher_course_student
(
    id           bigint           not null
        primary key,
    teacher_id   bigint           not null,
    teacher_name varchar(20)      null,
    course_id    bigint           not null,
    course_name  varchar(20)      null,
    student_id   bigint           not null,
    student_name varchar(20)      null,
    deleted      int(1) default 0 not null,
    create_time  datetime(6)      not null,
    update_time  datetime(6)      not null,
    create_user  bigint           not null,
    update_user  bigint           not null
);

