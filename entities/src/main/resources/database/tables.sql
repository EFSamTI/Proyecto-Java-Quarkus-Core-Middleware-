create user integrador with encrypted password 'eb503f97-7316-4b55-b8c7-54bb514d3d2f';

create database eurofish with owner integrador;

create table if not exists paas (
	id serial not null primary key,
	description character varying not null,
	webid uuid not null unique,
	ip character varying not null,
	port integer not null default 80,
	ssl bool not null default 'f',
	root_path character varying,
	timeout integer not null default 30000,
	body_as_header bool default 'f',
	body json,
	business_one bool not null default 'f',
	cookie character varying
);

alter table paas owner to integrador;

create table if not exists tx (
	id bigserial not null primary key,
	source_id integer not null references paas(id),
	destination_id integer not null references paas(id),
	operation character varying not null,
	"timestamp" timestamp without time zone not null default localtimestamp,
	verb character varying not null,
	path character varying not null,
	milliseconds integer not null default 0,
	error_code integer
);