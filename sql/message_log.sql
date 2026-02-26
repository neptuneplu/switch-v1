use
switch;
DROP TABLE IF EXISTS message_log;
CREATE TABLE message_log
(
    id          char(19)      not null,
    seq_no      char(19)      not null,
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

create unique index message_log_idx_1 on message_log (seq_no, direction);
create index message_log_idx_2 on message_log (pan);
create index message_log_idx_3 on message_log (terminal_id);