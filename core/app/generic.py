import json
from typing import Optional

from requests import request, Response

from core.app.common import Entry, LOG, ENV, MessageRequest


def generic_request(message: MessageRequest, paas: Entry) -> Optional[dict]:
    url = 'http{}://{}{}{}{}'.format(
        's' if paas.ssl else '',
        paas.ip,
        ':{}'.format(paas.port) if paas.port != 80 else '',
        paas.root_path,
        message.path
    )
    LOG.info(url, extra=ENV.logstash_extra)
    headers = paas.body if paas.body else None
    response: Response = request(
        message.verb,
        url,
        data=json.dumps(message.body),
        headers=headers,
        timeout=paas.timeout / 1000
    )
    if response.status_code == 200:
        return json.loads(response.content.decode('utf-8'))
    else:
        LOG.error('[{}] {}'.format(response.status_code, response.reason), extra=ENV.logstash_extra)
