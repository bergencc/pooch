import SwiftUI

struct LoginView: View {
    @EnvironmentObject var authVM: AuthViewModel
    @State private var email = ""
    @State private var password = ""
    @State private var showPassword = false
    @State private var showRegister = false
    @State private var showServerConfig = false

    var canSubmit: Bool {
        !email.isEmpty && password.count >= 6
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 32) {
                    // Logo / Hero
                    VStack(spacing: 16) {
                        ZStack {
                            Circle()
                                .fill(
                                    LinearGradient(
                                        colors: [.brandOrange, .brandOrangeDark],
                                        startPoint: .topLeading,
                                        endPoint: .bottomTrailing
                                    )
                                )
                                .frame(width: 100, height: 100)
                            Image(systemName: "pawprint.fill")
                                .font(.system(size: 44))
                                .foregroundStyle(.white)
                        }
                        .shadow(color: .brandOrange.opacity(0.4), radius: 12, y: 6)

                        Text("Pooch Scan")
                            .font(.largeTitle.bold())
                        Text("Smart product scanning for your dog")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    .padding(.top, 40)

                    // Form
                    VStack(spacing: 16) {
                        FloatingLabelField(
                            label: "Email",
                            text: $email,
                            icon: "envelope.fill",
                            keyboardType: .emailAddress,
                            textContentType: .emailAddress,
                            autocapitalization: .never
                        )

                        ZStack(alignment: .trailing) {
                            FloatingLabelField(
                                label: "Password",
                                text: $password,
                                icon: "lock.fill",
                                textContentType: .password,
                                isSecure: !showPassword
                            )
                            Button {
                                showPassword.toggle()
                            } label: {
                                Image(systemName: showPassword ? "eye.slash.fill" : "eye.fill")
                                    .foregroundStyle(.secondary)
                                    .padding(.trailing, 16)
                            }
                        }
                    }

                    // Error
                    if let error = authVM.errorMessage {
                        ErrorBanner(message: error)
                    }

                    // Login Button
                    Button {
                        Task { await authVM.login(email: email, password: password) }
                    } label: {
                        HStack {
                            if authVM.isLoading {
                                ProgressView().tint(.white)
                            } else {
                                Text("Sign In")
                                    .font(.headline)
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 52)
                        .background(canSubmit ? Color.brandOrange : Color.brandOrange.opacity(0.4))
                        .foregroundStyle(.white)
                        .clipShape(RoundedRectangle(cornerRadius: 14))
                    }
                    .disabled(!canSubmit || authVM.isLoading)

                    // Register
                    HStack {
                        Text("Don't have an account?")
                            .foregroundStyle(.secondary)
                        Button("Sign Up") {
                            showRegister = true
                        }
                        .foregroundStyle(.brandOrange)
                        .fontWeight(.semibold)
                    }
                    .font(.subheadline)

                    // Server config
                    Button {
                        showServerConfig = true
                    } label: {
                        HStack(spacing: 4) {
                            Image(systemName: "server.rack")
                            Text("Configure Server")
                        }
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    }

                    Spacer()
                }
                .padding(.horizontal, 24)
            }
            .navigationBarHidden(true)
            .sheet(isPresented: $showRegister) {
                RegisterView()
                    .environmentObject(authVM)
            }
            .sheet(isPresented: $showServerConfig) {
                ServerConfigView()
            }
        }
    }
}
