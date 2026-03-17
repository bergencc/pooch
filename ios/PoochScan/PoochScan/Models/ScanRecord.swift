import Foundation

struct ScanRecord: Codable, Identifiable {
    let id: String
    let dogId: String
    let product: Product?
    let recommendation: String?
    let createdAt: String

    enum CodingKeys: String, CodingKey {
        case id
        case dogId = "dog_id"
        case product
        case recommendation
        case createdAt = "created_at"
    }

    var formattedDate: String {
        let formatter = ISO8601DateFormatter()
        
        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
        
        if let date = formatter.date(from: createdAt) {
            let display = DateFormatter()
            
            display.dateStyle = .medium
            display.timeStyle = .short
            
            return display.string(from: date)
        }
        
        return createdAt
    }
}
