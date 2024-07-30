--FX retrieve_paas_per_bson
drop function if exists retrieve_paas_per_webid(character varying);

create or replace function retrieve_paas_per_webid(uuid) returns table (
	id integer,
	description character varying,
	web_id uuid,
	ip character varying,
	port integer,
	root_path character varying,
	timeout integer,
	body_as_header bool,
	body json,
	business_one bool,
	cookie character varying
) as 
$$
select id, description, webid, ip, port, root_path, timeout, body_as_header, body, business_one, cookie
from paas 
where webid = $1;
$$ language sql;


--select * from retrieve_paas_per_bson('')
--END FX retrieve_paas_per_bson