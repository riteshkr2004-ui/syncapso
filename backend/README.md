# SYNCAPSO AI Backend

Deploy this folder to a Node-compatible HTTPS host.

Environment variables:
- OPENAI_API_KEY: your OpenAI API key
- OPENAI_MODEL: gpt-5.6-luna
- PORT: supplied by your host, usually automatic

Health check:
GET /health

AI endpoint:
POST /api/ai

Keep OPENAI_API_KEY on the server. Never put it into the Android app.
