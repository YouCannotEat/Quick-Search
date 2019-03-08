--创建数据库

drop  table if exists FILE_INDEX;
create table if not exists file_index(
  name varchar(256) not null comment '文件名',
  path varchar(1024) not null comment '文件路径',
  depth int not null comment '文件深度',
  file_type varchar(256) not null comment '文件类型'
);