import SwiftUI

enum RecommendationSentiment {
    case safe, warning, danger

    var icon: String {
        switch self {
        case .safe: return "checkmark.shield.fill"
        case .warning: return "exclamationmark.triangle.fill"
        case .danger: return "xmark.octagon.fill"
        }
    }

    var color: Color {
        switch self {
        case .safe: return .green
        case .warning: return .orange
        case .danger: return .red
        }
    }

    var label: String {
        switch self {
        case .safe: return "Safe"
        case .warning: return "Caution"
        case .danger: return "Danger"
        }
    }
}
