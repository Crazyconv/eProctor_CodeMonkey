# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table allocation (
  allocation_id             integer auto_increment not null,
  capacity                  integer,
  course_id                 integer,
  slot_id                   integer,
  constraint pk_allocation primary key (allocation_id))
;

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
  slot_id                   integer auto_increment not null,
  start_time                datetime,
  end_time                  datetime,
  constraint pk_time_slot primary key (slot_id))
;


create table registration (
  stu_id                         integer not null,
  course_id                      integer not null,
  constraint pk_registration primary key (stu_id, course_id))
;
alter table allocation add constraint fk_allocation_course_1 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_allocation_course_1 on allocation (course_id);
alter table allocation add constraint fk_allocation_timeSlot_2 foreign key (slot_id) references time_slot (slot_id) on delete restrict on update restrict;
create index ix_allocation_timeSlot_2 on allocation (slot_id);
alter table question add constraint fk_question_course_3 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_question_course_3 on question (course_id);
alter table solution add constraint fk_solution_question_4 foreign key (ques_id) references question (ques_id) on delete restrict on update restrict;
create index ix_solution_question_4 on solution (ques_id);
alter table solution add constraint fk_solution_student_5 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;
create index ix_solution_student_5 on solution (stu_id);



alter table registration add constraint fk_registration_student_01 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;

alter table registration add constraint fk_registration_course_02 foreign key (course_id) references course (course_id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table allocation;

drop table course;

drop table registration;

drop table question;

drop table solution;

drop table student;

drop table time_slot;

SET FOREIGN_KEY_CHECKS=1;

