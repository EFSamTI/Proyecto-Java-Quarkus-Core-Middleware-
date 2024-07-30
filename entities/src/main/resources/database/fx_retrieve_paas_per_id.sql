--FX retrieve_paas_per_id
drop function if exists retrieve_paas_per_id(integer);

create or replace function retrieve_paas_per_id(integer) returns table (
	id integer,
	description character varying,
	web_id uuid,
	ip character varying,
	port integer,
	ssl bool,
	root_path character varying,
	timeout integer,
	body_as_header bool,
	body json,
	business_one bool,
	cookie character varying
) as 
$$
select id, description, webid, ip, port, ssl, root_path, timeout, body_as_header, body, business_one, cookie
from paas 
where id = $1;
$$ language sql;

alter function retrieve_paas_per_id(integer) owner to integrador;

--select * from retrieve_paas_per_id(2)
--END FX retrieve_paas_per_id