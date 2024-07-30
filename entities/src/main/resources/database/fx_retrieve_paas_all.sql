--FX: retrieve_paas_all
drop function if exists retrieve_paas_all();

create or replace function retrieve_paas_all() returns table (
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
from paas order by description;
$$ language sql;

alter function retrieve_paas_all() owner to integrador;

--select * from retrieve_paas_all()
--END FX retrieve_paas_all