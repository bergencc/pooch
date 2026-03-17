import SwiftUI

struct EcoScoreBadge: View {
    let score: String

    var color: Color {
        switch score.uppercased() {
        case "A": return Color(red: 0.12, green: 0.7, blue: 0.35)
        case "B": return Color(red: 0.45, green: 0.75, blue: 0.2)
        case "C": return Color(red: 0.85, green: 0.7, blue: 0.1)
        case "D": return Color(red: 0.95, green: 0.45, blue: 0.1)
        case "E", "F": return .red
        default: return .gray
        }
    }

    var body: some View {
        HStack(spacing: 4) {
            Image(systemName: "leaf.fill")
                .font(.caption)
            Text("Eco: \(score.uppercased())")
                .font(.caption.weight(.bold))
        }
        .padding(.horizontal, 10)
        .padding(.vertical, 5)
        .background(color.opacity(0.15))
        .foregroundStyle(color)
        .clipShape(Capsule())
    }
}
