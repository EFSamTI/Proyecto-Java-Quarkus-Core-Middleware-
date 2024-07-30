--FX: update_cookie
drop function if exists update_cookie(character varying, character varying);

create or replace function update_cookie(character varying, character varying)
returns void as
$$
begin
	if exists (select 1 from paas where web_id = $1) then
		--update
		update paas set
			cookie		= $2
		where id = $1;
	else
		raise notice '% do not exists!', $1;
	end if;
	--
end;
$$ language plpgsql;
--END: FX update_cookie