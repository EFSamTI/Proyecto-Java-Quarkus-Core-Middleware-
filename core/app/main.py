from socket import socket, AF_INET, SOCK_DGRAM

import uvicorn

from core.app.api import *
from core.app.common import LOG


def __get_ip() -> str:
    _ip = '127.0.0.1'
    _s = socket(AF_INET, SOCK_DGRAM)
    _s.settimeout(0)
    try:
        _s.connect(('8.8.8.8', 1))
        _ip = _s.getsockname()[0]
    finally:
        _s.close()
    return _ip


if __name__ == '__main__':
    ip = __get_ip()
    LOG.info(ip)
    try:
        uvicorn.run(app,
                    host=ip, port=8490,
                    ssl_certfile='../certs/eurofish_com_ec.pem',
                    ssl_keyfile='../certs/eurofish.key',
                    access_log=False)
    except Exception as e:
        LOG.error(e)
