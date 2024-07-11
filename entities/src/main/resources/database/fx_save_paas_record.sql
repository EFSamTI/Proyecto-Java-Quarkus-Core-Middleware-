--FX: save_paas_record
drop function if exists save_paas_record(text);

create or replace function save_paas_record(text)
returns void as
$$
declare v_id integer;
		v_desc character varying;
		v_bsonid character varying;
begin
	select id, description, bsonid
	into v_id, v_desc, v_bsonid
	from json_to_record($1::json) x (
		id integer,
		description character varying,
		bsonid character varying
	);
	--
	if exists (select 1 from paas where id = v_id) then
		--update
		update paas set
			description		= v_desc,
			bsonid			= v_bsonid
		where id = v_id;
	else
		--insert
		insert into paas (description, bsonid)
		values (v_desc, v_bsonid) 
		returning id into v_id;
	end if;
	--
end;
$$ language plpgsql;
--END: FX save_paas_record