# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chat (
  chat_id                   integer not null,
  from_student              boolean,
  message                   varchar(255),
  time                      timestamp,
  report_id                 integer,
  constraint pk_chat primary key (chat_id))
;

create table exam_record (
  exam_record_id            integer not null,
  exam_session_id           integer,
  student_id                integer,
  invi_id                   integer,
  report_id                 integer,
  constraint pk_exam_record primary key (exam_record_id))
;

create table image (
  image_id                  integer not null,
  time                      timestamp,
  picture_path              varchar(255),
  report_id                 integer,
  constraint pk_image primary key (image_id))
;

create table invigilator (
  invi_id                   integer not null,
  account                   varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  constraint pk_invigilator primary key (invi_id))
;

create table report (
  report_id                 integer not null,
  remark                    clob,
  exam_status               integer,
  constraint pk_report primary key (report_id))
;

create table setting (
  s_id                      integer not null,
  s_key                     varchar(255),
  s_value                   varchar(255),
  constraint pk_setting primary key (s_id))
;

create sequence chat_seq;

create sequence exam_record_seq;

create sequence image_seq;

create sequence invigilator_seq;

create sequence report_seq;

create sequence setting_seq;

alter table chat add constraint fk_chat_report_1 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_chat_report_1 on chat (report_id);
alter table exam_record add constraint fk_exam_record_invigilator_2 foreign key (invi_id) references invigilator (invi_id) on delete restrict on update restrict;
create index ix_exam_record_invigilator_2 on exam_record (invi_id);
alter table exam_record add constraint fk_exam_record_report_3 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_exam_record_report_3 on exam_record (report_id);
alter table image add constraint fk_image_report_4 foreign key (report_id) references report (report_id) on delete restrict on update restrict;
create index ix_image_report_4 on image (report_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists chat;

drop table if exists exam_record;

drop table if exists image;

drop table if exists invigilator;

drop table if exists report;

drop table if exists setting;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists chat_seq;

drop sequence if exists exam_record_seq;

drop sequence if exists image_seq;

drop sequence if exists invigilator_seq;

drop sequence if exists report_seq;

drop sequence if exists setting_seq;

