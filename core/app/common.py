import logging
from os import environ as env
from typing import Literal, Any

import logstash
from psycopg2.extras import register_uuid
from pydantic import BaseModel, Field
from urllib3 import disable_warnings


class EnvironmentValues:
    def __init__(self):
        disable_warnings()
        register_uuid()

    @property
    def logstash_host(self) -> str:
        return env.get('LOGSTASH_HOST')

    @property
    def logstash_port(self) -> int:
        return int(env.get('LOGSTASH_PORT'))

    @property
    def logstash_extra(self):
        return {
            'app_name': 'Core'
        }

    @property
    def postgres_host(self) -> str:
        return env.get('POSTGRES_HOST')

    @property
    def postgres_port(self) -> int:
        return int(env.get('POSTGRES_PORT'))

    @property
    def postgres_database(self) -> str:
        return env.get('POSTGRES_DATABASE')

    @property
    def postgres_user(self) -> str:
        return env.get('POSTGRES_USER')

    @property
    def postgres_password(self) -> str:
        return env.get('POSTGRES_PASSWORD')


class Logger:
    def __init__(self, name: str = 'Core'):
        self._logger = logging.getLogger(name)
        self._logger.setLevel(logging.DEBUG)
        # self._logger.addHandler(logging.StreamHandler())
        self._logger.addHandler(
            logstash.TCPLogstashHandler(
                ENV.logstash_host, ENV.logstash_port, version=1
            )
        )

    @property
    def logger(self) -> logging.Logger:
        return self._logger


class Entry:
    def __init__(self, **args):
        self._fields = args

    def __getattr__(self, item):
        if item not in self._fields:
            raise AttributeError
        return self._fields[item]

    def __setattr__(self, key, value):
        if key != '_fields':
            self._fields[key] = value
        super(Entry, self).__setattr__(key, value)

    def __repr__(self):
        fields = '; '.join('{}={}'.format(k, v) for k, v in sorted(self._fields.items()))
        return '{}({})'.format(self.__class__.__name__, fields)


class MessageBase(BaseModel):
    source: str
    destination: str
    operation: Literal['C', 'R', 'U', 'D']
    verb: str
    path: str
    body: Any | None = Field(default=None)


class Feedback(MessageBase):
    pass


class MessageRequest(MessageBase):
    feedback: Feedback | None = Field(default=None)


ENV = EnvironmentValues()
LOG = Logger().logger
