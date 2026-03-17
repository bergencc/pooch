import SwiftUI

struct ProfileView: View {
    @EnvironmentObject var authVM: AuthViewModel
    @State private var showLogoutAlert = false
    @State private var showServerConfig = false

    var body: some View {
        NavigationStack {
            List {
                // User Info Header
                Section {
                    HStack(spacing: 16) {
                        ZStack {
                            Circle()
                                .fill(
                                    LinearGradient(colors: [.brandOrange, .brandOrangeDark],
                                                   startPoint: .topLeading, endPoint: .bottomTrailing)
                                )
                                .frame(width: 64, height: 64)
                            Text(authVM.currentUser?.name?.prefix(1).uppercased() ?? "?")
                                .font(.title.bold())
                                .foregroundStyle(.white)
                        }
                        VStack(alignment: .leading, spacing: 4) {
                            Text(authVM.currentUser?.name ?? "User")
                                .font(.headline)
                            Text(authVM.currentUser?.email ?? "")
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                            
                            if authVM.currentUser?.role == "admin" {
                                Text("Admin")
                                    .font(.caption.weight(.bold))
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 2)
                                    .background(Color.purple.opacity(0.15))
                                    .foregroundStyle(.purple)
                                    .clipShape(Capsule())
                            }
                        }
                    }
                    .padding(.vertical, 8)
                }

                // Settings
                Section("App Settings") {
                    Button {
                        showServerConfig = true
                    } label: {
                        Label("Server Configuration", systemImage: "server.rack")
                            .foregroundStyle(.primary)
                    }

                    NavigationLink {
                        AboutView()
                    } label: {
                        Label("About Pooch Scan", systemImage: "info.circle")
                    }
                }

                // Privacy & Legal
                Section("Privacy & Data") {
                    Label("Your data is stored securely and never shared with third parties.",
                          systemImage: "lock.shield")
                        .font(.footnote)
                        .foregroundStyle(.secondary)
                }

                // Sign Out
                Section {
                    Button(role: .destructive) {
                        showLogoutAlert = true
                    } label: {
                        HStack {
                            Spacer()
                            Label("Sign Out", systemImage: "rectangle.portrait.and.arrow.right")
                                .fontWeight(.medium)
                            Spacer()
                        }
                    }
                }

                // App version
                Section {
                    HStack {
                        Spacer()
                        Text("Pooch Scan v1.0.0")
                            .font(.caption)
                            .foregroundStyle(.secondary)
                        Spacer()
                    }
                }
                .listRowBackground(Color.clear)
            }
            .navigationTitle("Profile")
            .alert("Sign Out", isPresented: $showLogoutAlert) {
                Button("Sign Out", role: .destructive) { authVM.logout() }
                Button("Cancel", role: .cancel) {}
            } message: {
                Text("Are you sure you want to sign out?")
            }
            .sheet(isPresented: $showServerConfig) {
                ServerConfigView()
            }
        }
    }
}
