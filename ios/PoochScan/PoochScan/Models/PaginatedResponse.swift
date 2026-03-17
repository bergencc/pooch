import Foundation

struct PaginatedResponse<T: Codable>: Codable {
    let items: [T]
    let total: Int
    let page: Int
    let pageSize: Int
    let pages: Int

    enum CodingKeys: String, CodingKey {
        case items, total, page
        case pageSize = "page_size"
        case pages
    }
}
