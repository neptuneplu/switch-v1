use
switch;
DROP TABLE IF EXISTS message_log;
CREATE TABLE message_log
(
    id          char(19)      not null,
    seq_no      char(19)      not null,
    direction   char(1)       not null,
    message_key varchar(19),
    hex_message varchar(9999) not null,
    create_date date          not null,
    create_time time          not null,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

create unique index message_log_idx_1 on message_log (seq_no, direction);
create index message_log_idx_2 on message_log (message_key);