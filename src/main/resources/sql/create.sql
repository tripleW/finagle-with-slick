create table if not exists helper (
  id int not null auto_increment,
  helper_id         varchar(128)  not null unique,
  home_group_id     varchar(128) not null,
  first_name        varchar(256),
  last_name         varchar(256),
  first_name_roman  varchar(256),
  last_name_roman   varchar(256),
  email             varchar(256),
  last_logged_in_at datetime,
  last_operated_at  datetime,
  created_by_service_name    varchar(256) default 'io.triplew.example' not null,
  created_by_service_user_id varchar(256) default '0' not null,
  created_at datetime not null default current_timestamp,
  updated_by_service_name    varchar(256) default 'io.triplew.example' not null,
  updated_by_service_user_id varchar(256) default '0' not null,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key(id)
)
;

create table if not exists home_group (
  id int not null auto_increment,
  home_group_id     varchar(128) not null,
  name              varchar(256),
  last_logged_in_at datetime,
  last_operated_at  datetime,
  created_by_service_name    varchar(256) default 'io.triplew.example' not null,
  created_by_service_user_id varchar(256) default '0' not null,
  created_at datetime not null default current_timestamp,
  updated_by_service_name    varchar(256) default 'io.triplew.example' not null,
  updated_by_service_user_id varchar(256) default '0' not null,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key(id)
)
;
