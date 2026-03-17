import SwiftUI
import Combine

@MainActor
class ScanHistoryViewModel: ObservableObject {
    @Published var records: [ScanRecord] = []
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var currentPage = 1
    @Published var totalPages = 1
    @Published var hasMore = false

    private let api = APIClient.shared

    func loadHistory(dogId: String, refresh: Bool = false) async {
        if refresh {
            currentPage = 1
            records = []
        }
        
        guard !isLoading else { return }
        
        isLoading = true
        errorMessage = nil
        
        do {
            let response = try await api.getScanHistory(dogId: dogId, page: currentPage)
            
            if refresh {
                records = response.items
            } else {
                records.append(contentsOf: response.items)
            }
            
            totalPages = response.pages
            hasMore = currentPage < totalPages
            currentPage += 1
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }

    func loadMore(dogId: String) async {
        guard hasMore else { return }
        
        await loadHistory(dogId: dogId)
    }
}
