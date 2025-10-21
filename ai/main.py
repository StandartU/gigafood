from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import JSONResponse
from PIL import Image

from ai.ai_module_classify import classify_food, classify_food_with_nutrition
from ai.ai_module_generate_text import generate_text

app = FastAPI(title="AI Modules API")

@app.post("/classify_food")
async def classify_food_endpoint(file: UploadFile = File(...)):
    try:
        image = Image.open(file.file).convert("RGB")
        dct = classify_food_with_nutrition(image)
        return JSONResponse(content=dct)
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})

@app.post("/generate_text")
async def generate_text_endpoint(
    prompt: str = Form(...),
    max_tokens: int = Form(200)
):
    try:
        text = generate_text(prompt, max_tokens)
        return {"generated_text": text}
    except Exception as e:
        return JSONResponse(status_code=500, content={"error": str(e)})