from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from common import LOG, ENV, MessageRequest
from generic import generic_request
from mosquitto import Mosquitto
from postgres import retrieve_paas
from service_layer import business_one_request

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=['*'],
    allow_credentials=True,
    allow_methods=['*'],
    allow_headers=['*'],
)


@app.post('/v1/api/message/generic')
async def generic_api(request: MessageRequest):
    try:
        paas = retrieve_paas(request.destination)
        if paas:
            LOG.info(paas, extra=ENV.logstash_extra)
            result = generic_request(request, paas)
            if not result:
                with Mosquitto() as mqtt:
                    mqtt.generic(request.model_dump())
            return result
    except Exception as e:
        LOG.error(str(e), extra=ENV.logstash_extra)
        return {'error': str(e)}


@app.post('/v1/api/message/business-one')
async def business_one_api(request: MessageRequest):
    try:
        paas = retrieve_paas(request.destination)
        if paas:
            LOG.info(paas, extra=ENV.logstash_extra)
            result = business_one_request(request, paas)
            if not result:
                with Mosquitto() as mqtt:
                    mqtt.business_one(request.model_dump())
            return result
    except Exception as e:
        LOG.error(str(e), extra=ENV.logstash_extra)
        return {'error': str(e)}
