import SwiftUI

struct EmptyDogsView: View {
    @Binding var showAddDog: Bool

    var body: some View {
        VStack(spacing: 24) {
            Image(systemName: "pawprint.circle")
                .font(.system(size: 72))
                .foregroundStyle(.brandOrange.opacity(0.6))
            VStack(spacing: 8) {
                Text("No Dogs Yet")
                    .font(.title2.bold())
                Text("Add your dog's profile to get personalized product recommendations tailored to their health needs.")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 40)
            }
            Button {
                showAddDog = true
            } label: {
                Label("Add Your First Dog", systemImage: "plus.circle.fill")
                    .font(.headline)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
            }
            .buttonStyle(.borderedProminent)
            .tint(.brandOrange)
            .padding(.horizontal, 40)
        }
    }
}
