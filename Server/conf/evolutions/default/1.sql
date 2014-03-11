# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chat (
  chat_id                   integer auto_increment not null,
  report_report_id          integer not null,
  sender                    varchar(255),
  message                   varchar(255),
  time                      datetime,
  constraint pk_chat primary key (chat_id))
;

create table course (
  course_id                 integer auto_increment not null,
  course_code               varchar(255),
  constraint pk_course primary key (course_id))
;

create table exam (
  exam_id                   integer auto_increment not null,
  course_course_id          integer,
  time_slot_time_slot_id    integer,
  student_student_id        integer,
  report_report_id          integer,
  constraint pk_exam primary key (exam_id))
;

create table invigilator (
  invigilator_id            integer auto_increment not null,
  account                   varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  constraint pk_invigilator primary key (invigilator_id))
;

create table question (
  question_id               integer auto_increment not null,
  course_course_id          integer not null,
  content                   longtext,
  constraint pk_question primary key (question_id))
;

create table report (
  report_id                 integer auto_increment not null,
  remark                    longtext,
  exam_status               tinyint(1) default 0,
  constraint pk_report primary key (report_id))
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
  capability                integer,
  constraint pk_time_slot primary key (time_slot_id))
;


create table exam_invigilator (
  exam_exam_id                   integer not null,
  invigilator_invigilator_id     integer not null,
  constraint pk_exam_invigilator primary key (exam_exam_id, invigilator_invigilator_id))
;

create table student_course (
  student_student_id             integer not null,
  course_course_id               integer not null,
  constraint pk_student_course primary key (student_student_id, course_course_id))
;
alter table chat add constraint fk_chat_report_1 foreign key (report_report_id) references report (report_id) on delete restrict on update restrict;
create index ix_chat_report_1 on chat (report_report_id);
alter table exam add constraint fk_exam_course_2 foreign key (course_course_id) references course (course_id) on delete restrict on update restrict;
create index ix_exam_course_2 on exam (course_course_id);
alter table exam add constraint fk_exam_timeSlot_3 foreign key (time_slot_time_slot_id) references time_slot (time_slot_id) on delete restrict on update restrict;
create index ix_exam_timeSlot_3 on exam (time_slot_time_slot_id);
alter table exam add constraint fk_exam_student_4 foreign key (student_student_id) references student (student_id) on delete restrict on update restrict;
create index ix_exam_student_4 on exam (student_student_id);
alter table exam add constraint fk_exam_report_5 foreign key (report_report_id) references report (report_id) on delete restrict on update restrict;
create index ix_exam_report_5 on exam (report_report_id);
alter table question add constraint fk_question_course_6 foreign key (course_course_id) references course (course_id) on delete restrict on update restrict;
create index ix_question_course_6 on question (course_course_id);
alter table solution add constraint fk_solution_question_7 foreign key (question_question_id) references question (question_id) on delete restrict on update restrict;
create index ix_solution_question_7 on solution (question_question_id);
alter table solution add constraint fk_solution_student_8 foreign key (student_student_id) references student (student_id) on delete restrict on update restrict;
create index ix_solution_student_8 on solution (student_student_id);
alter table time_slot add constraint fk_time_slot_course_9 foreign key (course_course_id) references course (course_id) on delete restrict on update restrict;
create index ix_time_slot_course_9 on time_slot (course_course_id);



alter table exam_invigilator add constraint fk_exam_invigilator_exam_01 foreign key (exam_exam_id) references exam (exam_id) on delete restrict on update restrict;

alter table exam_invigilator add constraint fk_exam_invigilator_invigilator_02 foreign key (invigilator_invigilator_id) references invigilator (invigilator_id) on delete restrict on update restrict;

alter table student_course add constraint fk_student_course_student_01 foreign key (student_student_id) references student (student_id) on delete restrict on update restrict;

alter table student_course add constraint fk_student_course_course_02 foreign key (course_course_id) references course (course_id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table chat;

drop table course;

drop table student_course;

drop table exam;

drop table exam_invigilator;

drop table invigilator;

drop table question;

drop table report;

drop table solution;

drop table student;

drop table time_slot;

SET FOREIGN_KEY_CHECKS=1;

