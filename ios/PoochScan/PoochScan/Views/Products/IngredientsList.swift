import SwiftUI

struct IngredientsList: View {
    let ingredients: [String]
    @State private var expanded = false

    static let dangerousIngredients = [
        "xylitol", "chocolate", "cocoa", "grape", "raisin", "garlic", "onion",
        "macadamia", "caffeine", "avocado", "nutmeg", "alcohol"
    ]

    var body: some View {
        let displayed = expanded ? ingredients : Array(ingredients.prefix(8))

        VStack(alignment: .leading, spacing: 6) {
            FlowLayout(spacing: 6) {
                ForEach(displayed, id: \.self) { ingredient in
                    let isDangerous = Self.dangerousIngredients.contains { ingredient.lowercased().contains($0) }
                    
                    Text(ingredient)
                        .font(.caption)
                        .padding(.horizontal, 10)
                        .padding(.vertical, 5)
                        .background(
                            (isDangerous ? Color.red : Color(.systemGray5))
                                .opacity(isDangerous ? 0.15 : 1)
                        )
                        .foregroundStyle(isDangerous ? .red : .primary)
                        .clipShape(Capsule())
                        .overlay(
                            isDangerous ? Capsule().stroke(Color.red.opacity(0.4), lineWidth: 1) : nil
                        )
                }
            }

            if ingredients.count > 8 {
                Button {
                    withAnimation(.spring(duration: 0.3)) { expanded.toggle() }
                } label: {
                    Text(expanded ? "Show less" : "Show all \(ingredients.count) ingredients")
                        .font(.caption.weight(.medium))
                        .foregroundStyle(.brandOrange)
                }
                .padding(.top, 4)
            }
        }
    }
}
