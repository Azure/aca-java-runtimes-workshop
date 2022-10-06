DROP TABLE IF EXISTS Statistics;
DROP SEQUENCE IF EXISTS statistics_seq;

create sequence statistics_seq start 1 increment 50;
create table Statistics
(
    id          int8 not null,
    description varchar(255),
    done_at     timestamp,
    duration    numeric(21,0),
    framework   int4,
    parameter   varchar(255),
    type        int4,
    primary key (id)
);
