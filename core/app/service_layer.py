import json
from typing import Optional

from requests import request, Response

from common import Entry, LOG, ENV, MessageRequest


def business_one_request(message: MessageRequest, paas: Entry) -> Optional[dict]:
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
        timeout=paas.timeout / 1000,
        verify=False
    )
    if response.status_code == 200:
        return json.loads(response.content.decode('utf-8'))
    elif response.status_code == 401:
        pass
    else:
        LOG.error('[{}] {}'.format(response.status_code, response.reason), extra=ENV.logstash_extra)
