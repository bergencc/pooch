import SwiftUI
import Combine

@MainActor
class ScanViewModel: ObservableObject {
    @Published var scannedProduct: Product?
    @Published var recommendation: String?
    @Published var isScanning = false
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var showResult = false
    @Published var lastBarcode: String?

    private let api = APIClient.shared

    func processBarcode(_ barcode: String, dogId: String) async {
        guard !isLoading, barcode != lastBarcode else { return }
        
        lastBarcode = barcode
        isLoading = true
        errorMessage = nil

        do {
            let response = try await api.scan(barcode: barcode, dogId: dogId)
            
            scannedProduct = response.product
            recommendation = response.recommendation
            showResult = true
        } catch APIError.serverError(404, _) {
            errorMessage = "Product not found for barcode: \(barcode)"
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }

    func reset() {
        scannedProduct = nil
        recommendation = nil
        showResult = false
        lastBarcode = nil
        errorMessage = nil
        isLoading = false
    }
}
