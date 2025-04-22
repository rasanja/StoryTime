const { onRequest } = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const logger = require("firebase-functions/logger");
const cors = require("cors")({ origin: true });
const { OpenAI } = require("openai");

// Declare the secret
const openAiKey = defineSecret("OPENAI_API_KEY");
const developerInstruction =
  "Always interpret the image content and use it meaningfully in the story. The prompt is only a guide — the story must reflect what's seen in the image.";

exports.generateStory = onRequest(
  {
    cors: true,
    secrets: [openAiKey], //  inject the secre
    memory: "512MiB",
    timeoutSeconds: 120,
  },
  async (req, res) => {
    const openai = new OpenAI({
      apiKey: openAiKey.value(),
    });

    try {
      const { prompt, imageBase64 } = req.body;

      if (!prompt || !imageBase64) {
        return res.status(400).json({ error: "Missing prompt or image." });
      }

      const response = await openai.chat.completions.create({
        model: "gpt-4-turbo",
        messages: [
          {
            role: "user",
            content: [
              { type: "text", text: developerInstruction },
              { type: "text", text: `Write a 200-word bedtime story based on this prompt:${prompt}` },
              { type: "image_url", image_url: { url: imageBase64 } },
            ],
          },
        ],
        max_tokens: 600,
        temperature: 0.8,
      });

      res.status(200).json({ story: response.choices[0].message.content });
    } catch (error) {
      logger.error("OpenAI error:", error);
      res.status(500).json({ error: "Something went wrong." });
    }
  }
);