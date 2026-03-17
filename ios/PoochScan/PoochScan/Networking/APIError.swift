import Foundation

enum APIError: LocalizedError {
    case invalidURL
    case noData
    case decodingError(String)
    case serverError(Int, String)
    case unauthorized
    case networkError(String)

    var errorDescription: String? {
        switch self {
        case .invalidURL: return "Invalid URL"
        case .noData: return "No data received"
        case .decodingError(let msg): return "Failed to decode: \(msg)"
        case .serverError(let code, let msg): return "Server error \(code): \(msg)"
        case .unauthorized: return "Session expired. Please log in again."
        case .networkError(let msg): return msg
        }
    }
}
