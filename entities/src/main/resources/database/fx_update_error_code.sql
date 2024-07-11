--FX update_error_code
create or replace function update_error_code(
	int8, int4
) returns void as
$$
declare v_ms int4;
begin
	select round((
			extract(epoch from localtimestamp) - 
			extract(epoch from "timestamp")
		) * 1000) into v_ms
	from tx where id = $1;
	--raise notice '%', v_ms;
	update tx 
	set 
		milliseconds = v_ms,
		error_code = $2
	where id = $1;
end;
$$ language plpgsql;
--END FX update_error_code