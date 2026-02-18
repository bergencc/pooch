"""
Seed script: populates the database with an admin user and ~50 initial products.
Run: python seed.py
"""
import sys, os
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from app.database import SessionLocal, engine, Base
import app.models.models  # noqa
from app.models.models import User, Product
from app.services.auth import hash_password

Base.metadata.create_all(bind=engine)

PRODUCTS = [
    {"barcode": "012345678901", "name": "Hill's Science Diet Adult", "brand": "Hill's", "product_type": "food",
     "ingredients": ["chicken", "whole grain wheat", "whole grain corn", "soybean meal", "pork fat"], "eco_score": "B"},
    {"barcode": "012345678902", "name": "Royal Canin Adult", "brand": "Royal Canin", "product_type": "food",
     "ingredients": ["chicken by-product meal", "brown rice", "brewers rice", "oat groats", "wheat gluten"], "eco_score": "C"},
    {"barcode": "012345678903", "name": "Purina Pro Plan Adult", "brand": "Purina", "product_type": "food",
     "ingredients": ["chicken", "rice flour", "corn gluten meal", "poultry by-product meal", "oat meal"], "eco_score": "C"},
    {"barcode": "012345678904", "name": "Blue Buffalo Life Protection", "brand": "Blue Buffalo", "product_type": "food",
     "ingredients": ["deboned chicken", "chicken meal", "brown rice", "barley", "oatmeal", "peas"], "eco_score": "B"},
    {"barcode": "012345678905", "name": "Wellness CORE Grain-Free", "brand": "Wellness", "product_type": "food",
     "ingredients": ["deboned chicken", "chicken meal", "turkey meal", "peas", "potatoes", "lentils"], "eco_score": "A"},
    {"barcode": "012345678906", "name": "Orijen Original", "brand": "Champion", "product_type": "food",
     "ingredients": ["deboned chicken", "deboned turkey", "yellowtail flounder", "whole eggs", "whole atlantic mackerel"], "eco_score": "A"},
    {"barcode": "012345678907", "name": "Taste of the Wild Pacific Stream", "brand": "Diamond Pet Foods", "product_type": "food",
     "ingredients": ["salmon", "ocean fish meal", "sweet potatoes", "potatoes", "peas", "potato protein"], "eco_score": "B"},
    {"barcode": "012345678908", "name": "Merrick Grain Free Real Chicken", "brand": "Merrick", "product_type": "food",
     "ingredients": ["deboned chicken", "chicken meal", "turkey meal", "sweet potatoes", "potatoes", "peas"], "eco_score": "B"},
    {"barcode": "012345678909", "name": "Acana Regionals Wild Atlantic", "brand": "Champion", "product_type": "food",
     "ingredients": ["whole atlantic mackerel", "whole atlantic herring", "whole monkfish", "whole acadian redfish", "whole flounder"], "eco_score": "A"},
    {"barcode": "012345678910", "name": "Fromm Four-Star Salmon Tunalini", "brand": "Fromm", "product_type": "food",
     "ingredients": ["salmon", "salmon meal", "tuna", "whole grain oatmeal", "whole grain barley", "peas"], "eco_score": "B"},
    # Treats
    {"barcode": "012345678911", "name": "Zuke's Mini Naturals Chicken", "brand": "Zuke's", "product_type": "treat",
     "ingredients": ["chicken", "chicken liver", "oat flour", "barley flour", "tapioca starch"], "eco_score": "B"},
    {"barcode": "012345678912", "name": "Blue Buffalo Wilderness Trail Treats", "brand": "Blue Buffalo", "product_type": "treat",
     "ingredients": ["chicken", "peas", "oat flour", "tapioca starch", "pea protein", "chickpea flour"], "eco_score": "B"},
    {"barcode": "012345678913", "name": "Milk-Bone Original Dog Biscuits", "brand": "Milk-Bone", "product_type": "treat",
     "ingredients": ["wheat flour", "wheat bran", "meat and bone meal", "beef fat", "salt", "dicalcium phosphate"], "eco_score": "D"},
    {"barcode": "012345678914", "name": "Greenies Original Dental Treats", "brand": "Greenies", "product_type": "treat",
     "ingredients": ["wheat starch", "wheat protein isolate", "glycerin", "water", "lecithin"], "eco_score": "C"},
    {"barcode": "012345678915", "name": "Beggin' Strips Bacon", "brand": "Purina", "product_type": "treat",
     "ingredients": ["ground wheat", "corn syrup", "meat by-products", "soy flour", "artificial bacon flavor", "salt"], "eco_score": "E"},
    {"barcode": "012345678916", "name": "Old Mother Hubbard Classic Crunchy Treats", "brand": "Old Mother Hubbard", "product_type": "treat",
     "ingredients": ["wheat flour", "oat flour", "cheddar cheese", "eggs", "apples", "carrots"], "eco_score": "B"},
    {"barcode": "012345678917", "name": "Wellness Soft WellBites Lamb & Salmon", "brand": "Wellness", "product_type": "treat",
     "ingredients": ["lamb", "salmon", "pea protein", "tapioca starch", "glycerin", "dried lamb liver"], "eco_score": "A"},
    {"barcode": "012345678918", "name": "Cloud Star Buddy Biscuits Bacon", "brand": "Cloud Star", "product_type": "treat",
     "ingredients": ["oat flour", "peas", "canola oil", "natural bacon flavor", "dried egg"], "eco_score": "B"},
    {"barcode": "012345678919", "name": "Natural Balance L.I.T. Sweet Potato & Fish", "brand": "Natural Balance", "product_type": "treat",
     "ingredients": ["sweet potato", "salmon", "tapioca starch", "canola oil", "rosemary extract"], "eco_score": "A"},
    {"barcode": "012345678920", "name": "Charlee Bear Liver Treats", "brand": "Charlee Bear", "product_type": "treat",
     "ingredients": ["liver", "rice flour", "oat flour", "brewers yeast"], "eco_score": "A"},
    # Supplements
    {"barcode": "012345678921", "name": "Cosequin DS Plus MSM", "brand": "Nutramax", "product_type": "supplement",
     "ingredients": ["glucosamine hydrochloride", "sodium chondroitin sulfate", "methylsulfonylmethane"], "eco_score": "C"},
    {"barcode": "012345678922", "name": "Zesty Paws Omega Bites", "brand": "Zesty Paws", "product_type": "supplement",
     "ingredients": ["wild alaskan salmon oil", "sunflower lecithin", "coconut oil", "hemp seed oil"], "eco_score": "B"},
    {"barcode": "012345678923", "name": "VetriScience Composure Chews", "brand": "VetriScience", "product_type": "supplement",
     "ingredients": ["thiamine", "l-theanine", "colostrum calming complex", "dried chicken liver"], "eco_score": "B"},
    {"barcode": "012345678924", "name": "Probiotic for Dogs FortiFlora", "brand": "Purina Pro Plan", "product_type": "supplement",
     "ingredients": ["animal digest", "enterococcus faecium", "dried skim milk", "dried egg product"], "eco_score": "C"},
    {"barcode": "012345678925", "name": "Vet's Best Healthy Coat Shed & Itch Relief", "brand": "Vet's Best", "product_type": "supplement",
     "ingredients": ["omega-3 fatty acids", "omega-6 fatty acids", "vitamin e", "flaxseed oil"], "eco_score": "B"},
    # Foods - Wet
    {"barcode": "012345678926", "name": "Royal Canin Gastrointestinal Wet", "brand": "Royal Canin", "product_type": "food",
     "ingredients": ["chicken", "chicken liver", "corn starch", "guar gum", "cassia gum", "sodium tripolyphosphate"], "eco_score": "C"},
    {"barcode": "012345678927", "name": "Hill's Prescription Diet i/d", "brand": "Hill's", "product_type": "food",
     "ingredients": ["water", "chicken", "pork liver", "rice", "corn starch", "soybean oil"], "eco_score": "C"},
    {"barcode": "012345678928", "name": "Merrick Backcountry Raw Infused Pacific Catch", "brand": "Merrick", "product_type": "food",
     "ingredients": ["salmon", "ocean whitefish", "sweet potato", "potato", "peas"], "eco_score": "A"},
    {"barcode": "012345678929", "name": "Wellness Complete Health Chicken & Sweet Potato", "brand": "Wellness", "product_type": "food",
     "ingredients": ["chicken", "chicken broth", "chicken liver", "sweet potatoes", "peas", "carrots"], "eco_score": "A"},
    {"barcode": "012345678930", "name": "Purina Beneful Incredibites", "brand": "Purina", "product_type": "food",
     "ingredients": ["chicken", "water sufficient for processing", "barley", "liver", "corn starch modified"], "eco_score": "D"},
    # Foods with allergen flags
    {"barcode": "012345678931", "name": "Diamond Naturals Beef Meal & Rice", "brand": "Diamond", "product_type": "food",
     "ingredients": ["beef meal", "rice", "egg product", "grain sorghum", "chicken fat", "beet pulp"], "eco_score": "C"},
    {"barcode": "012345678932", "name": "Iams ProActive Health Adult MiniChunks", "brand": "Iams", "product_type": "food",
     "ingredients": ["chicken", "chicken by-product meal", "corn meal", "ground whole grain sorghum", "beet pulp"], "eco_score": "C"},
    {"barcode": "012345678933", "name": "Nutro Wholesome Essentials Farm-Raised Chicken", "brand": "Nutro", "product_type": "food",
     "ingredients": ["chicken", "chicken meal", "whole brown rice", "rice bran", "split peas", "pea protein"], "eco_score": "B"},
    {"barcode": "012345678934", "name": "Canidae Pure Ancestral Raw Coated", "brand": "Canidae", "product_type": "food",
     "ingredients": ["turkey", "turkey meal", "chicken", "chicken meal", "peas", "lentils", "sweet potatoes"], "eco_score": "A"},
    {"barcode": "012345678935", "name": "Victor Hi-Pro Plus", "brand": "Victor", "product_type": "food",
     "ingredients": ["beef meal", "grain sorghum", "whole grain milo", "chicken fat", "dried beet pulp", "flaxseed"], "eco_score": "B"},
    # Medications (OTC)
    {"barcode": "012345678936", "name": "Benadryl Dye-Free Liquid", "brand": "Benadryl", "product_type": "medication",
     "ingredients": ["diphenhydramine hcl", "water", "glycerin", "sodium citrate"], "eco_score": "C"},
    {"barcode": "012345678937", "name": "Pepcid AC Original Strength", "brand": "Pepcid", "product_type": "medication",
     "ingredients": ["famotidine", "hydroxypropyl cellulose", "magnesium stearate", "microcrystalline cellulose"], "eco_score": "C"},
    {"barcode": "012345678938", "name": "Nexgard Spectra Chewable (Vet Rx)", "brand": "Boehringer Ingelheim", "product_type": "medication",
     "ingredients": ["afoxolaner", "milbemycin oxime", "microcrystalline cellulose", "natural beef flavour"], "eco_score": "C"},
    {"barcode": "012345678939", "name": "Frontline Plus Flea & Tick", "brand": "Merial", "product_type": "medication",
     "ingredients": ["fipronil 9.8%", "s-methoprene 8.8%", "butylated hydroxyanisole"], "eco_score": "D"},
    {"barcode": "012345678940", "name": "Sentinel Spectrum Chew", "brand": "Virbac", "product_type": "medication",
     "ingredients": ["milbemycin oxime", "lufenuron", "praziquantel", "natural beef flavour"], "eco_score": "C"},
    # Harmful ingredient examples (for testing recommendation engine)
    {"barcode": "099999999901", "name": "Test Product - Contains Xylitol", "brand": "Test Brand", "product_type": "treat",
     "ingredients": ["wheat flour", "xylitol", "sugar", "vanilla"], "eco_score": "F"},
    {"barcode": "099999999902", "name": "Test Product - Contains Garlic & Onion", "brand": "Test Brand", "product_type": "food",
     "ingredients": ["beef", "garlic powder", "onion powder", "salt", "water"], "eco_score": "F"},
    # More foods
    {"barcode": "012345678941", "name": "Pedigree Adult Complete Nutrition", "brand": "Pedigree", "product_type": "food",
     "ingredients": ["whole grain corn", "meat and bone meal", "corn gluten meal", "animal fat", "soybean meal"], "eco_score": "D"},
    {"barcode": "012345678942", "name": "Eukanuba Adult Medium Breed", "brand": "Eukanuba", "product_type": "food",
     "ingredients": ["chicken", "chicken by-product meal", "corn meal", "ground whole grain sorghum", "fish meal"], "eco_score": "C"},
    {"barcode": "012345678943", "name": "Nature's Recipe Easy to Digest Chicken & Rice", "brand": "Nature's Recipe", "product_type": "food",
     "ingredients": ["chicken", "chicken meal", "brown rice", "barley", "oatmeal", "beet pulp"], "eco_score": "B"},
    {"barcode": "012345678944", "name": "Authority Adult Chicken & Rice", "brand": "Authority", "product_type": "food",
     "ingredients": ["chicken", "ground rice", "chicken meal", "chicken fat", "dried egg product"], "eco_score": "B"},
    {"barcode": "012345678945", "name": "Instinct Raw Boost Grain-Free Chicken", "brand": "Nature's Variety", "product_type": "food",
     "ingredients": ["chicken", "turkey meal", "tapioca", "chicken fat", "ground flaxseed", "salmon oil"], "eco_score": "A"},
    {"barcode": "012345678946", "name": "Solid Gold Hundchen Flocken Puppy", "brand": "Solid Gold", "product_type": "food",
     "ingredients": ["lamb", "lamb meal", "whole grain oatmeal", "whole grain barley", "whole grain millet", "potatoes"], "eco_score": "B"},
    {"barcode": "012345678947", "name": "Lotus Grain Free Chicken Dry", "brand": "Lotus", "product_type": "food",
     "ingredients": ["chicken", "chicken meal", "chickpeas", "peas", "lentils", "duck", "tapioca"], "eco_score": "A"},
    {"barcode": "012345678948", "name": "Primal Freeze-Dried Nuggets Chicken", "brand": "Primal", "product_type": "food",
     "ingredients": ["chicken", "chicken necks", "chicken gizzards", "chicken livers", "organic carrots", "organic apples"], "eco_score": "A"},
    {"barcode": "012345678949", "name": "Stella & Chewy's Freeze-Dried Raw Dinner Patties", "brand": "Stella & Chewy's", "product_type": "food",
     "ingredients": ["beef", "beef heart", "beef liver", "beef bone", "organic pumpkin seed", "organic sunflower seed"], "eco_score": "A"},
    {"barcode": "012345678950", "name": "The Farmer's Dog Fresh Food Beef", "brand": "The Farmer's Dog", "product_type": "food",
     "ingredients": ["usda beef", "sweet potato", "lentils", "carrots", "kale", "beef liver", "sunflower oil"], "eco_score": "A",
     "nutrition_info": {"protein": "26%", "fat": "16%", "fiber": "2%", "moisture": "72%"}},
]


def seed():
    db = SessionLocal()
    try:
        # Create admin user
        if not db.query(User).filter(User.email == "admin@poochscan.com").first():
            admin = User(
                email="admin@poochscan.com",
                password_hash=hash_password("AdminPass123!"),
                name="Pooch Scan Admin",
                is_admin="admin",
            )
            db.add(admin)
            print("✅ Admin user created — admin@poochscan.com / AdminPass123!")

        # Create products
        added = 0
        for p in PRODUCTS:
            if not db.query(Product).filter(Product.barcode == p["barcode"]).first():
                product = Product(**p)
                db.add(product)
                added += 1

        db.commit()
        print(f"✅ {added} products seeded into database")
        print(f"ℹ️  Total products in DB: {db.query(Product).count()}")
    finally:
        db.close()


if __name__ == "__main__":
    seed()
