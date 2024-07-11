--FX add_tx_record
create or replace function add_tx_record(text)
returns int8 as
$$
declare
	o_id int8;
	v_source integer;
	v_dest integer;
	v_oper character varying;
	v_verb character varying;
	v_path character varying;
begin
	select "source", destination, operation, verb, "path"
	into v_source, v_dest, v_oper, v_verb, v_path
	from json_to_record($1::json) x (
		"source" int4,
		destination int4,
		operation character varying,
		verb character varying,
		"path" character varying
	);
	insert into tx (source_id, destination_id, operation, verb, path)
	values (v_source, v_dest, v_oper, v_verb, v_path)
	returning id into o_id;
	--
	return o_id;
end;
$$ language plpgsql;
--END FX add_tx_record