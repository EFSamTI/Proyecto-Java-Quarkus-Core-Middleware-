import json
import ssl

import paho.mqtt.client as mqtt

from core.app.common import LOG, ENV


def _on_connect(client, userdata, connect_flags, reason_code, properties):
    LOG.info('Connected' if reason_code == mqtt.MQTT_ERR_SUCCESS else 'No connected', extra=ENV.logstash_extra)


def _on_publish(client, userdata, mid, reason_code, properties):
    LOG.info('Message ID %d' % mid, extra=ENV.logstash_extra)


class Mosquitto:
    def __init__(self):
        self._client = mqtt.Client()  # mqtt.CallbackAPIVersion.VERSION1
        # certs_folder = abspath('../certs')
        # print(certs_folder)
        # ca_certs=join(certs_folder, 'ca.pem'),
        # certfile=join(certs_folder, 'eurofish_com_ec.pem'),
        # keyfile=join(certs_folder, 'eurofish.key'),
        self._client.on_connect = _on_connect
        self._client.on_publish = _on_publish
        self._client.disable_logger()
        self._client.tls_set(cert_reqs=ssl.CERT_NONE)
        self._client.username_pw_set(ENV.mosquitto_user, ENV.mosquitto_password)
        self._client.connect(ENV.mosquitto_host, ENV.mosquitto_port)

    def __enter__(self):
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        self._client.disconnect()

    def publish(self, topic: str, payload: dict) -> mqtt.MQTTMessageInfo:
        return self._client.publish(topic, json.dumps(payload).encode('utf-8'), qos=1)

    def generic(self, payload: dict) -> bool:
        r = self.publish('message/generic', payload)
        if r.rc == mqtt.MQTT_ERR_SUCCESS:
            LOG.info('Message ID {}: success'.format(r.mid), extra=ENV.logstash_extra)
        elif r.rc == mqtt.MQTT_ERR_NO_CONN:
            LOG.info('Message ID {}: the client is not currently connected'.format(r.mid), extra=ENV.logstash_extra)
        return r.is_published()

    def business_one(self, payload: dict) -> bool:
        return self.publish('message/business-one', payload).is_published()
