from typing import Optional, List, Tuple
from uuid import UUID

import psycopg2
from psycopg2.extras import RealDictCursor

from common import ENV, Entry


def __execute(sql: str) -> List[dict]:
    result = []
    with psycopg2.connect(
            host=ENV.postgres_host,
            port=ENV.postgres_port,
            database=ENV.postgres_database,
            user=ENV.postgres_user,
            password=ENV.postgres_password,
            cursor_factory=RealDictCursor,
            connect_timeout=3
    ) as conn:
        with conn.cursor() as cur:
            cur.execute(sql)
            for row in cur:
                result.append(dict(**row))
    return result


def __call_proc(name: str, values: Optional[Tuple]) -> None:
    with psycopg2.connect(
            host=ENV.postgres_host,
            port=ENV.postgres_port,
            database=ENV.postgres_database,
            user=ENV.postgres_user,
            password=ENV.postgres_password,
            connect_timeout=3
    ) as conn:
        with conn.cursor() as cur:
            cur.callproc(name, values)
        conn.commit()


def __call_proc_execute(name: str, values: Optional[Tuple]) -> List[dict]:
    result = []
    with psycopg2.connect(
            host=ENV.postgres_host,
            port=ENV.postgres_port,
            database=ENV.postgres_database,
            user=ENV.postgres_user,
            password=ENV.postgres_password,
            cursor_factory=RealDictCursor,
            connect_timeout=3
    ) as conn:
        with conn.cursor() as cur:
            cur.callproc(name, values)
            for row in cur:
                result.append(dict(**row))
    return result


def retrieve_paas(web_id: str) -> Optional[Entry]:
    data = __call_proc_execute('retrieve_paas_per_webid', (UUID(web_id),))
    if len(data) > 0:
        _t = type('PaaS', (Entry,), {})
        return _t(**data[0])


def update_cookie(web_id: UUID, cookie: str) -> Optional[Entry]:
    data = __call_proc_execute('update_cookie', (web_id, cookie))
    if len(data) > 0:
        _t = type('PaaS', (Entry,), {})
        return _t(**data[0])
