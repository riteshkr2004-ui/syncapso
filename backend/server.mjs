import 'dotenv/config';
import express from 'express';
import OpenAI from 'openai';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const app = express();
const port = process.env.PORT || 3000;

if (!process.env.OPENAI_API_KEY) {
  console.warn('OPENAI_API_KEY is not set.');
}

app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET,POST,OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-Type');
  if (req.method === 'OPTIONS') return res.sendStatus(204);
  next();
});
app.use(express.json({limit:'256kb'}));

const client = new OpenAI({
  apiKey: process.env.OPENAI_API_KEY,
  baseURL: 'https://openrouter.ai/api/v1'
});
const SYSTEM_PROMPT = `
You are SYNCAPSO AI Tutor, a focused NEET preparation assistant.
Help students with Physics, Chemistry and Biology concepts, numericals,
revision, study planning, backlog management, and exam strategy.
Be accurate and educational. Do not invent facts, formulas, NCERT statements, or PYQs.
Prefer NCERT-aligned explanations for Biology and Chemistry when relevant.
Explain difficult concepts step-by-step and use simple Hinglish/Hindi if the student does.
For numerical questions, show formula, substitution, units, and final answer.
If a question depends on an image or information you cannot see, say so rather than guessing.
Use the supplied SYNCAPSO study context only for personalization.
Keep answers focused but useful.
`;

app.get('/health', (_, res) => res.json({ok:true, service:'SYNCAPSO AI'}));

app.post('/api/ai', async (req,res)=>{
  try {
    const {message,history=[],studyContext={}} = req.body || {};
    if (typeof message !== 'string' || !message.trim()) {
      return res.status(400).json({error:'Message is required.'});
    }

    const recent = Array.isArray(history)
      ? history.slice(-10)
          .filter(x => x && typeof x.content === 'string')
          .map(x => ({
            role: x.role === 'assistant' ? 'assistant' : 'user',
            content: x.content.slice(0,6000)
          }))
      : [];

    const contextText = JSON.stringify(studyContext).slice(0,16000);

    const response = await client.responses.create({
      model: process.env.OPENAI_MODEL || 'gpt-5.6-luna',
      instructions: SYSTEM_PROMPT,
      input: [
        ...recent,
        {
          role: 'user',
          content:
            `SYNCAPSO STUDY CONTEXT (use only for personalization):\n${contextText}\n\nSTUDENT MESSAGE:\n${message}`
        }
      ],
      max_output_tokens: 1200
    });

    res.json({reply: response.output_text || 'I could not generate a response.'});
  } catch (err) {
    console.error(err);
    res.status(500).json({
      error:'The AI service could not respond. Check the server API key, billing, model, and logs.'
    });
  }
});

app.listen(port, () => console.log(`SYNCAPSO AI backend listening on ${port}`));
