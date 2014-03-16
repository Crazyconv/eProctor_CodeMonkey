# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chat (
  chat_id                   integer auto_increment not null,
  from_student              tinyint(1) default 0,
  sender_id                 integer,
  receiver_id               integer,
  message                   varchar(255),
  time                      datetime,
  report_id                 integer,
  constraint pk_chat primary key (chat_id))
;

create table course (
  course_id                 integer auto_increment not null,
  course_code               varchar(255),
  title                     varchar(255),
  constraint pk_course primary key (course_id))
;

create table exam (
  exam_id                   integer auto_increment not null,
  start_time                datetime,
  end_time                  datetime,
  course_id                 integer,
  stu_id                    integer,
  invi_id                   integer,
  report_id                 integer,
  constraint pk_exam primary key (exam_id))
;

create table invigilator (
  invi_id                   integer auto_increment not null,
  account                   varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  constraint pk_invigilator primary key (invi_id))
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

create table report (
  report_id                 integer auto_increment not null,
  remark                    longtext,
  exam_status               tinyint(1) default 0,
  constraint pk_report primary key (report_id))
;

create table solution (
  sol_id                    integer auto_increment not null,
  ques_id                   integer,
  answer                    varchar(255),
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

alter table chat add constraint fk_chat_report_1 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_chat_report_1 on chat (report_id);
alter table exam add constraint fk_exam_course_2 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_exam_course_2 on exam (course_id);
alter table exam add constraint fk_exam_student_3 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;
create index ix_exam_student_3 on exam (stu_id);
alter table exam add constraint fk_exam_invigilator_4 foreign key (invi_id) references invigilator (invi_id) on delete restrict on update restrict;
create index ix_exam_invigilator_4 on exam (invi_id);
alter table exam add constraint fk_exam_report_5 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_exam_report_5 on exam (report_id);
alter table question add constraint fk_question_course_6 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_question_course_6 on question (course_id);
alter table registration add constraint fk_registration_student_7 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;
create index ix_registration_student_7 on registration (stu_id);
alter table registration add constraint fk_registration_course_8 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_registration_course_8 on registration (course_id);
alter table solution add constraint fk_solution_question_9 foreign key (ques_id) references question (ques_id) on delete restrict on update restrict;
create index ix_solution_question_9 on solution (ques_id);
alter table solution add constraint fk_solution_student_10 foreign key (stu_id) references student (stu_id) on delete restrict on update restrict;
create index ix_solution_student_10 on solution (stu_id);
alter table time_slot add constraint fk_time_slot_course_11 foreign key (course_id) references course (course_id) on delete restrict on update restrict;
create index ix_time_slot_course_11 on time_slot (course_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table chat;

drop table course;

drop table exam;

drop table invigilator;

drop table question;

drop table registration;

drop table report;

drop table solution;

drop table student;

drop table time_slot;

SET FOREIGN_KEY_CHECKS=1;

