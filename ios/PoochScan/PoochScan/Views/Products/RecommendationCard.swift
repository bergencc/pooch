import SwiftUI

struct RecommendationCard: View {
    let text: String

    var sentiment: RecommendationSentiment {
        let lower = text.lowercased()
        
        if lower.contains("dangerous") || lower.contains("toxic") || lower.contains("unsafe") ||
           lower.contains("avoid") || lower.contains("harmful") {
            return .danger
        } else if lower.contains("caution") || lower.contains("warning") || lower.contains("allerg") ||
                  lower.contains("consult") {
            return .warning
        } else {
            return .safe
        }
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 10) {
                Image(systemName: sentiment.icon)
                    .font(.title3)
                    .foregroundStyle(sentiment.color)
                Text("Recommendation")
                    .font(.headline)
                Spacer()
                Text(sentiment.label)
                    .font(.caption.weight(.semibold))
                    .padding(.horizontal, 10)
                    .padding(.vertical, 4)
                    .background(sentiment.color.opacity(0.15), in: Capsule())
                    .foregroundStyle(sentiment.color)
            }
            Text(text)
                .font(.subheadline)
                .foregroundStyle(.primary.opacity(0.85))
                .fixedSize(horizontal: false, vertical: true)
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 14)
                .fill(sentiment.color.opacity(0.06))
                .overlay(
                    RoundedRectangle(cornerRadius: 14)
                        .stroke(sentiment.color.opacity(0.3), lineWidth: 1.5)
                )
        )
    }
}
