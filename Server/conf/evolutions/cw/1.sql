# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table course (
  course_id                 integer auto_increment not null,
  course_code               varchar(255),
  title                     varchar(255),
  question_no               integer,
  instruction               longtext,
  constraint pk_course primary key (course_id))
;

create table question (
  ques_id                   integer auto_increment not null,
  content                   longtext,
  course_id                 integer,
  constraint pk_question primary key (ques_id))
;

create table registration (
  reg_id                    integer auto_increment not null,
  stu_id                    integer,
  course_id                 integer,
  constraint pk_registration primary key (reg_id))
;

create table solution (
  sol_id                    integer auto_increment not null,
  ques_id                   integer,
  answer                    longtext,
  stu_id                    integer,
  constraint pk_solution primary key (sol_id))
;

create table student (
  stu_id                    integer auto_increment not null,
  matric_no                 varchar(255),
  password                  varchar(255),
  photo_path                varchar(255),
  constraint pk_student primary key (stu_id))
;

create table time_slot (
  time_id                   integer auto_increment not null,
  start_time                datetime,
  end_time                  datetime,
  course_id                 integer,
  capacity                  integer,
  constraint pk_time_slot primary key (time_id))
;

alter table question add constraint fk_question_course_1 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_question_course_1 on question (course_id);
alter table registration add constraint fk_registration_student_2 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;
create index ix_registration_student_2 on registration (stu_id);
alter table registration add constraint fk_registration_course_3 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_registration_course_3 on registration (course_id);
alter table solution add constraint fk_solution_question_4 foreign key (ques_id) references question (ques_id) on delete restrict on update restrict;
create index ix_solution_question_4 on solution (ques_id);
alter table solution add constraint fk_solution_student_5 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;
create index ix_solution_student_5 on solution (stu_id);
alter table time_slot add constraint fk_time_slot_course_6 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_time_slot_course_6 on time_slot (course_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table course;

drop table question;

drop table registration;

drop table solution;

drop table student;

drop table time_slot;

SET FOREIGN_KEY_CHECKS=1;

