FROM python:3.14-slim

ENV PYTHONUNBUFFERED=1
WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY uira_crawler ./uira_crawler

ENTRYPOINT ["python", "-m", "uira_crawler"]
