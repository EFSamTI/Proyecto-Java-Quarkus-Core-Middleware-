--FX: save_paas_record
drop function if exists save_paas_record(text);

create or replace function save_paas_record(text)
returns void as
$$
declare v_id integer;
		v_desc character varying;
		v_webid uuid;
		v_ip character varying;
		v_port integer;
		v_ssl bool;
		v_path character varying;
		v_timeout integer;
		v_business bool;
		v_as_header bool;
		v_body json;
begin
	select id, description, web_id, ip, port, ssl, root_path, timeout, business_one, body_as_header, body
	into v_id, v_desc, v_webid, v_ip, v_port, v_ssl, v_path, v_timeout, v_business, v_as_header, v_body
	from json_to_record($1::json) x (
		id integer,
		description character varying,
		web_id uuid,
		ip character varying,
		port integer,
		ssl bool,
		root_path character varying,
		timeout integer,
		business_one bool,
		body_as_header bool,
		body json
	);
	--
	if exists (select 1 from paas where id = v_id) then
		--update
		update paas set
			description		= v_desc,
			ip				= v_ip,
			port			= v_port,
			ssl				= v_ssl,
			root_path		= v_path,
			timeout			= v_timeout,
			body_as_header	= v_as_header,
			body			= v_body,
			business_one	= v_business
		where id = v_id;
	else
		select gen_random_uuid() into v_webid;
		--insert
		insert into paas (description, webid, ip, port, ssl, root_path, timeout, body_as_header, body, business_one)
		values (v_desc, v_webid, v_ip, v_port, v_ssl, v_path, v_timeout, v_as_header, v_body, v_business) 
		returning id into v_id;
	end if;
	--
end;
$$ language plpgsql;

alter function save_paas_record(text) owner to integrador;
--END: FX save_paas_record