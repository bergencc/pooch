import SwiftUI

struct NutritionGrid: View {
    let facts: [String: String]

    var body: some View {
        LazyVGrid(columns: [.init(.flexible()), .init(.flexible())], spacing: 8) {
            ForEach(Array(facts.keys.sorted()), id: \.self) { key in
                HStack {
                    Text(key)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                    Spacer()
                    Text(facts[key] ?? "—")
                        .font(.caption.weight(.semibold))
                }
                .padding(10)
                .background(Color(.systemGray6))
                .clipShape(RoundedRectangle(cornerRadius: 8))
            }
        }
    }
}
