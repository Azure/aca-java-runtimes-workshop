DROP TABLE IF EXISTS Statistics_Quarkus;
DROP TABLE IF EXISTS Statistics_Micronaut;
DROP TABLE IF EXISTS Statistics_Springboot;
DROP SEQUENCE IF EXISTS hibernate_sequence;
DROP SEQUENCE IF EXISTS statistics_springboot_seq;

create sequence hibernate_sequence start 1 increment 1;

create sequence statistics_springboot_seq start 1 increment 50;

create table Statistics_Quarkus
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

create table Statistics_Micronaut
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


create table Statistics_Springboot
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
