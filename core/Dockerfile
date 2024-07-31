FROM python:3.12
LABEL author="John, johnmoscoso911@gmail.com"

RUN pip install --upgrade pip
RUN pip install fastapi uvicorn paho-mqtt requests psycopg2-binary python-logstah

ENV TZ America/Guayaquil

EXPOSE 8490

COPY app /app

CMD [ "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8490", "--workers", "10", "--no-access-log" ]