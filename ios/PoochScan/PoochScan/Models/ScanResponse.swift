import Foundation

struct ScanResponse: Codable {
    let scan: ScanRecord
    let product: Product
    let recommendation: String
}
