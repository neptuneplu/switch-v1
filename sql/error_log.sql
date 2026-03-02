use
switch;
DROP TABLE IF EXISTS error_log;
CREATE TABLE error_log
(
    id          char(19)      not null,
    direction   char(1)       not null,
    pan         varchar(19)   not null,
    stan        char(6)       not null,
    aiic        char(11)      not null,
    rrn         char(12)      not null,
    terminal_id char(8)       not null,
    merchant_id char(15)      not null,
    hex_message varchar(9999) not null,
    create_date date          not null,
    create_time time          not null,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

