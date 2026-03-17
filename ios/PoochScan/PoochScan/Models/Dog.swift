import Foundation

struct Dog: Codable, Identifiable {
    let id: String
    var name: String
    var breed: String?
    var age: Int?
    var weight: Double?
    var activityLevel: String?
    var allergies: [String]
    var healthConditions: [String]
    var photoUrl: String?
    let userId: String
    let createdAt: String
    
    enum CodingKeys: String, CodingKey {
        case id, name, breed, age, weight
        case activityLevel = "activity_level"
        case allergies
        case healthConditions = "health_conditions"
        case photoUrl = "photo_url"
        case userId = "user_id"
        case createdAt = "created_at"
    }
    
    init(
        id: String = "",
        name: String,
        breed: String? = nil,
        age: Int? = nil,
        weight: Double? = nil,
        activityLevel: String? = nil,
        allergies: [String] = [],
        healthConditions: [String] = [],
        photoUrl: String? = nil,
        userId: String = "",
        createdAt: String = ""
    ) {
            self.id = id
            self.name = name
            self.breed = breed
            self.age = age
            self.weight = weight
            self.activityLevel = activityLevel
            self.allergies = allergies
            self.healthConditions = healthConditions
            self.photoUrl = photoUrl
            self.userId = userId
            self.createdAt = createdAt
        }
}
