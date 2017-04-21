# Finagle with Slick example

```bash
docker run -d -p 3307:3306 \
  --name finagle \
  -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \
  -e MYSQL_USER=finagle \
  -e MYSQL_PASSWORD=finagle \
  -e MYSQL_DATABASE=finagle \
  mysql \
  --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci 
```

```bash
mysql -uroot -p -h127.0.0.1 -P3307 -Dfinagle
```

```SQL
drop table if exists helper;
create table helper (
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
  created_by_service_name    varchar(256) default 'io.triplew.exapmle' not null,
  created_by_service_user_id varchar(256) default '0' not null,
  created_at datetime not null default current_timestamp,
  updated_by_service_name    varchar(256) default 'io.triplew.example' not null,
  updated_by_service_user_id varchar(256) default '0' not null,
  updated_at datetime not null default current_timestamp on update current_timestamp,
  primary key(id)
)
;
```

```SQL
insert into home_group (home_group_id, name)
values
('1', 'Triple W Group')
;

insert into helper (helper_id, home_group_id, first_name, last_name)
values
('1', '1', '吉永', 'さゆり')
;
```