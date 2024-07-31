import json

import paho.mqtt.client as mqtt

from common import LOG, ENV


def _on_connect(_client: mqtt.Client, _userdata, _flags, rc):
    LOG.info('Connected' if rc == mqtt.MQTT_ERR_SUCCESS else 'No connected', extra=ENV.logstash_extra)


def _on_publish(_client: mqtt.Client, _userdata, mid):
    LOG.info('Message ID %d' % mid, extra=ENV.logstash_extra)


class Mosquitto:
    def __init__(self):
        self._client = mqtt.Client('Core')
        self._client.on_connect = _on_connect
        self._client.on_publish = _on_publish
        self._client.disable_logger()
        self._client.username_pw_set(ENV.mosquitto_user, ENV.mosquitto_password)
        self._client.connect(ENV.mosquitto_host, ENV.mosquitto_port)

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self._client.disconnect()

    def _publish(self, topic: str, payload: dict) -> mqtt.MQTTMessageInfo:
        return self._client.publish(topic, json.dumps(payload).encode('utf-8'), qos=1)

    def generic(self, payload: dict) -> bool:
        r = self._publish('message/generic', payload)
        if r.rc == mqtt.MQTT_ERR_SUCCESS:
            LOG.info('Message ID {}: success'.format(r.mid), extra=ENV.logstash_extra)
        elif r.rc == mqtt.MQTT_ERR_NO_CONN:
            LOG.info('Message ID {}: the client is not currently connected'.format(r.mid), extra=ENV.logstash_extra)
        return r.is_published()

    def business_one(self, payload: dict) -> bool:
        return self._publish('message/business-one', payload).is_published()
