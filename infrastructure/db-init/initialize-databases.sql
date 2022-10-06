DROP TABLE IF EXISTS Statistics;
DROP SEQUENCE IF EXISTS hibernate_sequence;

create sequence hibernate_sequence start 1 increment 1;
create table Statistics
(
    id          int8 not null,
    description varchar(255),
    done_at     timestamp,
    duration    int8,
    framework   int4,
    parameter   varchar(255),
    type        int4,
    primary key (id)
);
