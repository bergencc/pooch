import SwiftUI

struct NoDogsBanner: View {
    var body: some View {
        HStack(spacing: 16) {
            Image(systemName: "pawprint.circle.fill")
                .font(.system(size: 32))
                .foregroundStyle(.brandOrange)
            VStack(alignment: .leading, spacing: 4) {
                Text("No dogs yet")
                    .font(.headline)
                Text("Add a dog profile to get personalized recommendations")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}
