from ollama import chat, ChatResponse, pull
import time

MODEL_NAME = "gemma3:1b"


def generate_text(prompt: str, max_tokens: int = 200):
    response: ChatResponse = chat(
        model=MODEL_NAME,
        messages=[
            {
                "role": "user",
                "content": prompt,
            }
        ],
        options={
            "num_predict": max_tokens,
            "temperature": 0.7,
            "top_p": 0.9
        }
    )

    return response.message.content
