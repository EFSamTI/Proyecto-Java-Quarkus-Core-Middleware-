--FX: update_cookie
drop function if exists update_cookie(uuid, character varying);

create or replace function update_cookie(uuid, character varying) returns table (
	id integer,
	description character varying,
	web_id uuid,
	ip character varying,
	port integer,
	ssl bool,
	root_path character varying,
	timeout integer,
	body_as_header bool,
	body json,
	business_one bool,
	cookie character varying
) as 
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
	return query
		select x.id, x.description, x.webid, x.ip, x.port, x.ssl, x.root_path, x.timeout, x.body_as_header, x.body, x.business_one, x.cookie
		from paas x
		where x.webid = $1;
end;
$$ language plpgsql;

alter function update_cookie(uuid, character varying) owner to integrador;
--END: FX update_cookie