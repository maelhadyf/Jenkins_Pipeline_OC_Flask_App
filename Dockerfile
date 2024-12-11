FROM python:3.11-slim

WORKDIR /app

# Copy only necessary files
COPY requirements.txt .
COPY app.py .

# Install dependencies and clean up in same layer
RUN pip install --no-cache-dir -r requirements.txt && \
    rm -rf /root/.cache/pip && \
    useradd -m appuser && \
    chown -R appuser:appuser /app

USER appuser

# Environment variable to prevent Python from writing pyc files
ENV PYTHONDONTWRITEBYTECODE=1
# Environment variable to prevent Python from buffering stdout and stderr
ENV PYTHONUNBUFFERED=1

EXPOSE 5000

CMD ["python", "app.py"]
