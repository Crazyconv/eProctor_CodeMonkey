# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chat (
  chat_id                   integer auto_increment not null,
  from_student              tinyint(1) default 0,
  message                   varchar(255),
  time                      datetime,
  report_id                 integer,
  constraint pk_chat primary key (chat_id))
;

create table exam (
  exam_id                   integer auto_increment not null,
  start_time                datetime,
  end_time                  datetime,
  course_id                 integer,
  student_id                integer,
  invi_id                   integer,
  report_id                 integer,
  constraint pk_exam primary key (exam_id))
;

create table image (
  image_id                  integer auto_increment not null,
  time                      datetime,
  picture_path              varchar(255),
  report_id                 integer,
  constraint pk_image primary key (image_id))
;

create table invigilator (
  invi_id                   integer auto_increment not null,
  account                   varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  constraint pk_invigilator primary key (invi_id))
;

create table report (
  report_id                 integer auto_increment not null,
  remark                    longtext,
  exam_status               integer,
  constraint pk_report primary key (report_id))
;

create table setting (
  s_id                      integer auto_increment not null,
  s_key                     varchar(255),
  s_value                   varchar(255),
  constraint pk_setting primary key (s_id))
;

alter table chat add constraint fk_chat_report_1 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_chat_report_1 on chat (report_id);
alter table exam add constraint fk_exam_invigilator_2 foreign key (invi_id) references invigilator (invi_id) on delete restrict on update restrict;
create index ix_exam_invigilator_2 on exam (invi_id);
alter table exam add constraint fk_exam_report_3 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_exam_report_3 on exam (report_id);
alter table image add constraint fk_image_report_4 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_image_report_4 on image (report_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table chat;

drop table exam;

drop table image;

drop table invigilator;

drop table report;

drop table setting;

SET FOREIGN_KEY_CHECKS=1;

