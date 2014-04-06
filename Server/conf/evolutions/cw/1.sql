# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table answer (
  answer_id                 integer auto_increment not null,
  ques_id                   integer,
  answer                    longtext,
  stu_id                    integer,
  constraint pk_answer primary key (answer_id))
;

create table course (
  course_id                 integer auto_increment not null,
  course_code               varchar(255),
  title                     varchar(255),
  question_no               integer,
  instruction               longtext,
  constraint pk_course primary key (course_id))
;

create table exam_session (
  exam_session_id           integer auto_increment not null,
  capacity                  integer,
  course_id                 integer,
  slot_id                   integer,
  constraint pk_exam_session primary key (exam_session_id))
;

create table question (
  ques_id                   integer auto_increment not null,
  content                   longtext,
  course_id                 integer,
  constraint pk_question primary key (ques_id))
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
alter table answer add constraint fk_answer_question_1 foreign key (ques_id) references question (ques_id) on delete restrict on update restrict;
create index ix_answer_question_1 on answer (ques_id);
alter table answer add constraint fk_answer_student_2 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;
create index ix_answer_student_2 on answer (stu_id);
alter table exam_session add constraint fk_exam_session_course_3 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_exam_session_course_3 on exam_session (course_id);
alter table exam_session add constraint fk_exam_session_timeSlot_4 foreign key (slot_id) references time_slot (slot_id) on delete restrict on update restrict;
create index ix_exam_session_timeSlot_4 on exam_session (slot_id);
alter table question add constraint fk_question_course_5 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_question_course_5 on question (course_id);



alter table registration add constraint fk_registration_student_01 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;

alter table registration add constraint fk_registration_course_02 foreign key (course_id) references course (course_id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table answer;

drop table course;

drop table registration;

drop table exam_session;

drop table question;

drop table student;

drop table time_slot;

SET FOREIGN_KEY_CHECKS=1;

