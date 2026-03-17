import Foundation

struct User: Codable, Identifiable {
    let id: String
    let email: String
    let name: String?
    let role: String
    let createdAt: String
    
    enum CodingKeys: String, CodingKey {
        case id, email, name, role
        case createdAt = "created_at"
    }
}
