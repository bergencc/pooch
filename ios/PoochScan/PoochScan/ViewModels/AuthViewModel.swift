import SwiftUI
import Combine

@MainActor
class AuthViewModel: ObservableObject {
    @Published var isAuthenticated = false
    @Published var currentUser: User?
    @Published var isLoading = false
    @Published var errorMessage: String?

    private let api = APIClient.shared

    init() {
        if api.token != nil {
            Task { await validateToken() }
        }
    }

    func validateToken() async {
        guard api.token != nil else { return }
        
        do {
            let user = try await api.getMe()
            
            self.currentUser = user
            self.isAuthenticated = true
        } catch {
            api.token = nil
            self.isAuthenticated = false
        }
    }

    func login(email: String, password: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            let response = try await api.login(email: email, password: password)
            
            api.token = response.accessToken
            currentUser = response.user
            isAuthenticated = true
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }

    func register(email: String, password: String, name: String) async {
        isLoading = true
        errorMessage = nil
        
        do {
            let response = try await api.register(email: email, password: password, name: name)
            
            api.token = response.accessToken
            currentUser = response.user
            isAuthenticated = true
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }

    func logout() {
        api.token = nil
        currentUser = nil
        isAuthenticated = false
    }
}
