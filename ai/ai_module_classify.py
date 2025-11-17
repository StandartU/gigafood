import requests
from transformers import AutoImageProcessor, AutoModelForImageClassification
from torch.nn.functional import softmax
import re
from PIL import Image
import torch
from pathlib import Path

BASE_DIR = Path(__file__).parent.resolve()
MODEL_PATH = BASE_DIR / "models" / "food-category-classification-v2.0"

processor = AutoImageProcessor.from_pretrained(MODEL_PATH)
model = AutoModelForImageClassification.from_pretrained(MODEL_PATH)
model.eval()


def classify_food(image: Image.Image) -> dict:
    inputs = processor(images=image, return_tensors="pt")
    with torch.no_grad():
        logits = model(**inputs).logits
    probs = softmax(logits, dim=-1)
    conf, pred_id = probs.max(-1)
    return {"label": model.config.id2label[pred_id.item()], "confidence": conf.item()}


def normalize_food_name(name: str):
    return re.sub(r'[_\-]', ' ', name).title()


def get_nutrition_info(food_name: str):
    search_url = "https://world.openfoodfacts.org/cgi/search.pl"
    params = {
        "search_terms": normalize_food_name(food_name),
        "search_simple": 1,
        "action": "process",
        "json": 1,
        "page_size": 1
    }

    response = requests.get(search_url, params=params)
    data = response.json()

    if data["count"] == 0:
        return {
            "weight_g": None,
            "calories": None,
            "protein": None,
            "fat": None,
            "carbs": None
        }

    product = data["products"][0]
    nutriments = product.get("nutriments", {})

    weight = nutriments.get("serving_size_g", 100)

    def scale(nutrient_name):
        value_per_100g = nutriments.get(nutrient_name)
        if value_per_100g is None:
            return None
        return round(value_per_100g * weight / 100)

    return {
        "weight_g": weight,
        "calories": scale("energy-kcal_100g"),
        "protein": scale("proteins_100g"),
        "fat": scale("fat_100g"),
        "carbs": scale("carbohydrates_100g")
    }


def classify_food_with_nutrition(image: Image.Image):
    result = classify_food(image)
    label = result["label"]
    nutrition = get_nutrition_info(label)
    return {
        "name": label,
        **nutrition,
        "confidence": result["confidence"]
    }
