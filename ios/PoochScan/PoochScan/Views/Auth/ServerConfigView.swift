import SwiftUI

struct ServerConfigView: View {
    @Environment(\.dismiss) var dismiss
    @State private var serverURL = UserDefaults.standard.string(forKey: "serverURL") ?? "http://localhost:8000"

    var body: some View {
        NavigationStack {
            Form {
                Section {
                    TextField("Server URL", text: $serverURL)
                        .keyboardType(.URL)
                        .autocorrectionDisabled()
                        .autocapitalization(.none)
                } header: {
                    Text("Backend Server URL")
                } footer: {
                    Text("Enter the base URL of your PoochScan backend (e.g. http://192.168.1.10:8000)")
                }
            }
            .navigationTitle("Server Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Save") {
                        UserDefaults.standard.set(serverURL, forKey: "serverURL")
                        dismiss()
                    }
                    .fontWeight(.semibold)
                }
                ToolbarItem(placement: .topBarLeading) {
                    Button("Cancel") { dismiss() }
                }
            }
        }
    }
}
