from ollama import chat, ChatResponse, pull
import time

MODEL_NAME = "gemma3:1b"

for i in range(10):
    try:
        pull(model=MODEL_NAME)
        print("Connected to Ollama")
        break
    except Exception as e:
        print(f"Failed to connect to Ollama, retrying... ({i+1}/10)")
        time.sleep(3)
else:
    raise RuntimeError("Cannot connect to Ollama after multiple attempts")


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
