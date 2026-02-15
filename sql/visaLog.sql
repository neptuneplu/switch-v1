use
switch;
DROP TABLE IF EXISTS visa_log;
CREATE TABLE visa_log
(
    id          char(19) binary not null,
    seq_no      char(19) binary not null,
    direction   char(1) binary not null,
    message_key varchar(19),
    hex_message varchar(9999) binary not null,
    create_date date not null,
    create_time time not null,
    PRIMARY KEY (id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

create unique index visa_log_idx_1 on visa_log (seq_no,direction);
create index visa_log_idx_2 on visa_log (message_key);