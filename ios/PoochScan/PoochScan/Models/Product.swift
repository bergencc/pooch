import Foundation

struct Product: Codable, Identifiable {
    let id: String
    let barcode: String
    let name: String
    let brand: String?
    let productType: String?
    let ingredients: [String]
    let nutritionInfo: [String: String]?
    let ecoScore: String?
    let photoUrl: String?
    let createdAt: String

    enum CodingKeys: String, CodingKey {
        case id, barcode, name, brand
        case productType = "product_type"
        case ingredients
        case nutritionInfo = "nutrition_info"
        case ecoScore = "eco_score"
        case photoUrl = "photo_url"
        case createdAt = "created_at"
    }

    var ecoScoreColor: String {
        switch ecoScore?.uppercased() {
        case "A": return "ecoA"
        case "B": return "ecoB"
        case "C": return "ecoC"
        case "D": return "ecoD"
        case "E": return "ecoE"
        case "F": return "ecoF"
        default: return "secondary"
        }
    }

    var productTypeDisplay: String {
        switch productType?.lowercased() {
        case "dry_food": return "Dry Food"
        case "wet_food": return "Wet Food"
        case "treat": return "Treat"
        case "supplement": return "Supplement"
        case "medication": return "Medication"
        default: return productType?.capitalized ?? "Unknown"
        }
    }
}
