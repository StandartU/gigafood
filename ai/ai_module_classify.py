import requests
from transformers import AutoFeatureExtractor, ResNetForImageClassification
from PIL import Image
import torch

MODEL_NAME = "microsoft/resnet-50"
MODEL_FOOD101 = "karakuri-ai/resnet50-food101"

extractor = AutoFeatureExtractor.from_pretrained(MODEL_FOOD101)
model = ResNetForImageClassification.from_pretrained(MODEL_FOOD101)

def classify_food(image: Image.Image) -> str:
    inputs = extractor(images=image, return_tensors="pt")
    with torch.no_grad():
        logits = model(**inputs).logits
    pred_id = logits.argmax(-1).item()
    return model.config.id2label[pred_id]

def get_nutrition_info(food_name: str):
    search_url = "https://world.openfoodfacts.org/cgi/search.pl"
    params = {
        "search_terms": food_name,
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

    # Получаем порцию (реальный вес)
    weight = nutriments.get("serving_size_g")
    if weight is None:
        weight = 100  # если порция не указана, ставим 100 г

    # Берём БЖУ на 100 г и пересчитываем на вес порции
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
    label = classify_food(image)
    nutrition = get_nutrition_info(label)
    return {
        "name": label,
        **nutrition
    }