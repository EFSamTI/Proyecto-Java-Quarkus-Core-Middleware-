--FX: retrieve_paas_all
drop function if exists retrieve_paas_all();

create or replace function retrieve_paas_all() returns table (
	id integer,
	description character varying,
	bsonid character varying
) as 
$$
select id, description, bsonid
from paas order by description;
$$ language sql;

--select * from retrieve_paas_all()
--END FX retrieve_paas_all