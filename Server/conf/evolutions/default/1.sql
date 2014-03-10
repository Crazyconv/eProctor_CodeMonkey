# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table chat (
  chat_id                   integer auto_increment not null,
  sender                    varchar(255),
  message                   varchar(255),
  time                      datetime,
  constraint pk_chat primary key (chat_id))
;

create table invigilator (
  invigilator_id            integer auto_increment not null,
  account                   varchar(255),
  password                  varchar(255),
  name                      varchar(255),
  constraint pk_invigilator primary key (invigilator_id))
;

create table report (
  report_id                 integer auto_increment not null,
  remark                    longtext,
  exam_status               tinyint(1) default 0,
  constraint pk_report primary key (report_id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table chat;

drop table invigilator;

drop table report;

SET FOREIGN_KEY_CHECKS=1;

