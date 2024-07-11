--FX retrieve_paas_per_id
drop function if exists retrieve_paas_per_id(integer);

create or replace function retrieve_paas_per_id(integer) returns table (
	id integer,
	description character varying,
	bsonid character varying
) as 
$$
select id, description, bsonid
from paas 
where id = $1;
$$ language sql;

--select * from retrieve_paas_per_id(2)
--END FX retrieve_paas_per_id