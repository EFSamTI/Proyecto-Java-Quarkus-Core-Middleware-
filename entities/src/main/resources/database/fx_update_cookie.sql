--FX: update_cookie
drop function if exists update_cookie(uuid, character varying);

create or replace function update_cookie(uuid, character varying)
returns void as
$$
begin
	if exists (select 1 from paas where webid = $1) then
		--update
		update paas set
			cookie		= $2
		where webid = $1;
	else
		raise notice '% do not exists!', $1;
	end if;
	--
end;
$$ language plpgsql;

alter function update_cookie(uuid, character varying) owner to integrador;
--END: FX update_cookie