--FX: retrieve_paas_list_per_bson
create or replace function retrieve_paas_list_per_bson(text) returns table (
	id integer,
	description character varying,
	bsonid character varying
) as
$$
select id, description, bsonid
from paas
where bsonid = any(string_to_array($1, ','));
$$ language sql;
--END FX: retrieve_paas_list_per_bson