import SwiftUI

struct ScanRecordRow: View {
    let record: ScanRecord

    var sentiment: RecommendationSentiment {
        guard let rec = record.recommendation else { return .safe }
        
        let lower = rec.lowercased()
        
        if lower.contains("dangerous") || lower.contains("toxic") || lower.contains("avoid") {
            return .danger
        } else if lower.contains("caution") || lower.contains("warning") || lower.contains("allerg") {
            return .warning
        }
        
        return .safe
    }

    var body: some View {
        HStack(spacing: 14) {
            // Product Image
            ZStack {
                RoundedRectangle(cornerRadius: 10)
                    .fill(Color(.systemGray5))
                    .frame(width: 52, height: 52)
                
                if let urlStr = record.product?.photoUrl, let url = URL(string: urlStr) {
                    AsyncImage(url: url) { img in
                        img.resizable().scaledToFill()
                    } placeholder: {
                        Image(systemName: "bag.fill")
                            .foregroundStyle(.secondary)
                    }
                    .frame(width: 52, height: 52)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                } else {
                    Image(systemName: "bag.fill")
                        .foregroundStyle(.secondary)
                }
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(record.product?.name ?? "Unknown Product")
                    .font(.subheadline.weight(.medium))
                    .lineLimit(1)
                
                if let brand = record.product?.brand {
                    Text(brand)
                        .font(.caption)
                        .foregroundStyle(.secondary)
                }
                HStack(spacing: 6) {
                    Image(systemName: sentiment.icon)
                        .font(.caption2)
                        .foregroundStyle(sentiment.color)
                    Text(sentiment.label)
                        .font(.caption2.weight(.semibold))
                        .foregroundStyle(sentiment.color)
                    Text("·")
                        .foregroundStyle(.secondary)
                    Text(record.formattedDate)
                        .font(.caption2)
                        .foregroundStyle(.secondary)
                }
            }
        }
        .padding(.vertical, 4)
    }
}
