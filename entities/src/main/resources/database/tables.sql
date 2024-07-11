create table if not exists paas (
	id serial not null primary key,
	description character varying not null,
	bsonid character varying not null unique
);

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