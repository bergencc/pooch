import Foundation

struct DogCreateRequest: Codable {
    var name: String
    var breed: String?
    var age: Int?
    var weight: Double?
    var activityLevel: String?
    var allergies: [String]
    var healthConditions: [String]

    enum CodingKeys: String, CodingKey {
        case name, breed, age, weight
        case activityLevel = "activity_level"
        case allergies
        case healthConditions = "health_conditions"
    }
}
