import SwiftUI

@main
struct PoochScanApp: App {
    @StateObject private var authViewModel = AuthViewModel()

    var body: some Scene {
        WindowGroup {
            AppRootView()
                .environmentObject(authViewModel)
        }
    }
}
