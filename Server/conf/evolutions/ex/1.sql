# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table course (
  course_id                 integer auto_increment not null,
  course_code               varchar(255),
  constraint pk_course primary key (course_id))
;

create table question (
  question_id               integer auto_increment not null,
  course_course_id          integer not null,
  content                   longtext,
  constraint pk_question primary key (question_id))
;

create table solution (
  solution_id               integer auto_increment not null,
  question_question_id      integer,
  answer                    varchar(255),
  student_student_id        integer,
  constraint pk_solution primary key (solution_id))
;

create table student (
  student_id                integer auto_increment not null,
  matric_no                 varchar(255),
  password                  varchar(255),
  photo_path                varchar(255),
  constraint pk_student primary key (student_id))
;

create table time_slot (
  time_slot_id              integer auto_increment not null,
  course_course_id          integer not null,
  start_time                datetime,
  end_time                  datetime,
  limit                     integer,
  constraint pk_time_slot primary key (time_slot_id))
;


create table student_course (
  student_student_id             integer not null,
  course_course_id               integer not null,
  constraint pk_student_course primary key (student_student_id, course_course_id))
;
alter table question add constraint fk_question_course_1 foreign key (course_course_id) references course (course_id) on delete restrict on update restrict;
create index ix_question_course_1 on question (course_course_id);
alter table solution add constraint fk_solution_question_2 foreign key (question_question_id) references question (question_id) on delete restrict on update restrict;
create index ix_solution_question_2 on solution (question_question_id);
alter table solution add constraint fk_solution_student_3 foreign key (student_student_id) references student (student_id) on delete restrict on update restrict;
create index ix_solution_student_3 on solution (student_student_id);
alter table time_slot add constraint fk_time_slot_course_4 foreign key (course_course_id) references course (course_id) on delete restrict on update restrict;
create index ix_time_slot_course_4 on time_slot (course_course_id);



alter table student_course add constraint fk_student_course_student_01 foreign key (student_student_id) references student (student_id) on delete restrict on update restrict;

alter table student_course add constraint fk_student_course_course_02 foreign key (course_course_id) references course (course_id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table course;

drop table question;

drop table solution;

drop table student;

drop table student_course;

drop table time_slot;

SET FOREIGN_KEY_CHECKS=1;

