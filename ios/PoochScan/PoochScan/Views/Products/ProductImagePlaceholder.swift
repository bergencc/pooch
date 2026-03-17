import SwiftUI

struct ProductImagePlaceholder: View {
    let type: String?

    var icon: String {
        switch type?.lowercased() {
        case "dry_food", "wet_food": return "fork.knife"
        case "treat": return "birthday.cake.fill"
        case "supplement": return "pills.fill"
        case "medication": return "cross.case.fill"
        default: return "bag.fill"
        }
    }

    var body: some View {
        ZStack {
            Color(.systemGray5)
            Image(systemName: icon)
                .font(.system(size: 60))
                .foregroundStyle(.brandOrange.opacity(0.6))
        }
    }
}
