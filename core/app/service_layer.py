import json
from typing import Optional

from requests import request, post, Response

from common import Entry, LOG, ENV, MessageRequest
from postgres import update_cookie


def login(paas: Entry) -> Optional[Entry]:
    login_url = 'https://{}{}{}/Login'.format(
        paas.ip,
        ':{}'.format(paas.port) if paas.port != 80 else '',
        paas.root_path
    )
    LOG.info(login_url, extra=ENV.logstash_extra)
    login_response: Response = post(
        login_url,
        data=json.dumps(paas.body),
        timeout=paas.timeout / 1000,
        verify=False
    )
    if login_response.status_code == 200:
        cookie = login_response.headers.get('Set-Cookie')
        LOG.info(cookie, extra=ENV.logstash_extra)
        return update_cookie(paas.web_id, cookie)


def business_one_request(message: MessageRequest, paas: Entry, count: int = 0) -> Optional[dict]:
    url = 'https://{}{}{}{}'.format(
        paas.ip,
        ':{}'.format(paas.port) if paas.port != 80 else '',
        paas.root_path,
        message.path
    )
    LOG.info(url, extra=ENV.logstash_extra)
    response: Response = request(
        message.verb,
        url,
        data=json.dumps(message.body),
        headers={'Cookie': paas.cookie},
        timeout=paas.timeout / 1000,
        verify=False
    )
    if response.status_code == 200:
        return json.loads(response.content.decode('utf-8'))
    elif response.status_code == 401:
        if count < 3:
            return business_one_request(message, login(paas), count + 1)
    else:
        LOG.error('[{}] {}'.format(response.status_code, response.reason), extra=ENV.logstash_extra)
