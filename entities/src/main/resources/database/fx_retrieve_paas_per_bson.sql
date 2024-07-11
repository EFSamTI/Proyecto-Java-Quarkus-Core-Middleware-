--FX retrieve_paas_per_bson
drop function if exists retrieve_paas_per_bson(character varying);

create or replace function retrieve_paas_per_bson(character varying) returns table (
	id integer,
	description character varying,
	bsonid character varying
) as 
$$
select id, description, bsonid
from paas 
where bsonid = $1;
$$ language sql;

--select * from retrieve_paas_per_bson('')
--END FX retrieve_paas_per_bson