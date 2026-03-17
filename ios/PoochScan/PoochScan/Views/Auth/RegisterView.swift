import SwiftUI

struct RegisterView: View {
    @EnvironmentObject var authVM: AuthViewModel
    @Environment(\.dismiss) var dismiss

    @State private var name = ""
    @State private var email = ""
    @State private var password = ""
    @State private var confirmPassword = ""
    @State private var showPassword = false

    var passwordsMatch: Bool { password == confirmPassword }
    var canSubmit: Bool {
        !name.isEmpty && !email.isEmpty && password.count >= 8 && passwordsMatch
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 24) {
                    VStack(spacing: 8) {
                        Text("Create Account")
                            .font(.title.bold())
                        Text("Join Pooch Scan and keep your dog healthy")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                    }
                    .padding(.top, 24)

                    VStack(spacing: 16) {
                        FloatingLabelField(
                            label: "Full Name",
                            text: $name,
                            icon: "person.fill",
                            textContentType: .name
                        )
                        FloatingLabelField(
                            label: "Email",
                            text: $email,
                            icon: "envelope.fill",
                            keyboardType: .emailAddress,
                            textContentType: .emailAddress,
                            autocapitalization: .never
                        )
                        FloatingLabelField(
                            label: "Password (min 8 chars)",
                            text: $password,
                            icon: "lock.fill",
                            textContentType: .newPassword,
                            isSecure: !showPassword
                        )
                        FloatingLabelField(
                            label: "Confirm Password",
                            text: $confirmPassword,
                            icon: "lock.fill",
                            textContentType: .newPassword,
                            isSecure: !showPassword
                        )

                        if !confirmPassword.isEmpty && !passwordsMatch {
                            HStack {
                                Image(systemName: "exclamationmark.triangle.fill")
                                Text("Passwords do not match")
                            }
                            .font(.caption)
                            .foregroundStyle(.red)
                            .frame(maxWidth: .infinity, alignment: .leading)
                        }

                        Toggle(isOn: $showPassword) {
                            Text("Show passwords")
                                .font(.subheadline)
                        }
                        .tint(.brandOrange)
                    }

                    if let error = authVM.errorMessage {
                        ErrorBanner(message: error)
                    }

                    Button {
                        Task { await authVM.register(email: email, password: password, name: name) }
                    } label: {
                        HStack {
                            if authVM.isLoading {
                                ProgressView().tint(.white)
                            } else {
                                Text("Create Account")
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
                }
                .padding(.horizontal, 24)
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Cancel") { dismiss() }
                }
            }
        }
    }
}

