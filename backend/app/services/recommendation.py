"""
Recommendation engine for dog product safety analysis.
Checks allergens, harmful ingredients, and provides health-based guidance.
"""
from app.models.models import Dog, Product


# Known harmful ingredients for dogs
HARMFUL_INGREDIENTS = {
    "xylitol": "DANGER: Xylitol is extremely toxic to dogs — can cause liver failure and death.",
    "chocolate": "DANGER: Chocolate is toxic to dogs and can be fatal.",
    "cocoa": "DANGER: Cocoa/chocolate derivatives are toxic to dogs.",
    "grapes": "DANGER: Grapes can cause kidney failure in dogs.",
    "raisins": "DANGER: Raisins can cause kidney failure in dogs.",
    "onion": "WARNING: Onions are toxic to dogs and can cause anemia.",
    "garlic": "WARNING: Garlic is toxic to dogs, especially in large amounts.",
    "macadamia": "WARNING: Macadamia nuts are toxic to dogs.",
    "alcohol": "DANGER: Alcohol is extremely dangerous for dogs.",
    "caffeine": "WARNING: Caffeine is toxic to dogs.",
    "avocado": "WARNING: Avocado can be harmful to dogs.",
    "nutmeg": "WARNING: Nutmeg is toxic to dogs.",
}

# Ingredients to watch for dogs with certain conditions
CONDITION_WARNINGS = {
    "diabetes": ["sugar", "corn syrup", "honey", "molasses", "fructose"],
    "kidney disease": ["phosphorus", "sodium", "potassium"],
    "heart disease": ["sodium", "salt"],
    "obesity": ["fat", "oil", "lard", "butter"],
    "pancreatitis": ["fat", "oil", "lard", "butter", "cream"],
}

ECO_SCORE_DESCRIPTIONS = {
    "A": "Excellent sustainability — this product has minimal environmental impact.",
    "B": "Good sustainability — minor environmental concerns.",
    "C": "Average sustainability — moderate environmental impact.",
    "D": "Below average sustainability — notable environmental concerns.",
    "E": "Poor sustainability — significant environmental impact.",
    "F": "Very poor sustainability — high environmental impact.",
}


def generate_recommendation(dog: Dog, product: Product) -> str:
    """Generate a detailed health and eco recommendation for a dog-product pair."""
    warnings = []
    positive_notes = []
    allergen_alerts = []

    ingredients_lower = [i.lower() for i in (product.ingredients or [])]
    dog_allergies = [a.lower() for a in (dog.allergies or [])]
    dog_conditions = [c.lower() for c in (dog.health_conditions or [])]

    # Check harmful ingredients
    for ingredient_key, warning_msg in HARMFUL_INGREDIENTS.items():
        for ing in ingredients_lower:
            if ingredient_key in ing:
                warnings.append(warning_msg)

                break

    # Check dog's known allergies against ingredients
    for allergy in dog_allergies:
        for ing in ingredients_lower:
            if allergy in ing or ing in allergy:
                allergen_alerts.append(
                    f"ALLERGEN ALERT: This product contains '{ing}' which matches "
                    f"{dog.name}'s known allergy to '{allergy}'."
                )

                break

    # Check health condition-specific warnings
    for condition in dog_conditions:
        for cond_key, flagged_ingredients in CONDITION_WARNINGS.items():
            if cond_key in condition:
                for flagged in flagged_ingredients:
                    for ing in ingredients_lower:
                        if flagged in ing:
                            warnings.append(
                                f"HEALTH WARNING: '{ing}' may be problematic for dogs "
                                f"with {cond_key}. Consult your vet."
                            )

                            break

    # Eco score note
    eco_note = None

    if product.eco_score and product.eco_score in ECO_SCORE_DESCRIPTIONS:
        eco_note = f"Eco Rating {product.eco_score}: {ECO_SCORE_DESCRIPTIONS[product.eco_score]}"

    # Build recommendation text
    parts = [f"Product analysis for {dog.name}: {product.name} by {product.brand or 'Unknown Brand'}\n"]

    if allergen_alerts:
        parts.append("ALLERGEN ALERTS")
        parts.extend(allergen_alerts)

    if warnings:
        parts.append("SAFETY WARNINGS")
        parts.extend(warnings)

    if not allergen_alerts and not warnings:
        parts.append(
            f"No known harmful ingredients or allergen conflicts detected for {dog.name}. "
            "Always read labels and consult your vet for personalized advice."
        )

    if eco_note:
        parts.append(f"\nSUSTAINABILITY\n{eco_note}")

    if allergen_alerts or warnings:
        parts.append(
            "\n⚕️ Please consult your veterinarian before giving this product to your dog."
        )

    return "\n".join(parts)
