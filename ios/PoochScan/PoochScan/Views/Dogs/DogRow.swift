import SwiftUI

struct DogRow: View {
    let dog: Dog
    let isSelected: Bool

    var body: some View {
        HStack(spacing: 14) {
            // Avatar
            ZStack {
                Circle()
                    .fill(isSelected ? Color.brandOrange : Color(.systemGray5))
                    .frame(width: 52, height: 52)
                
                if let urlStr = dog.photoUrl, let url = URL(string: urlStr) {
                    AsyncImage(url: url) { img in
                        img.resizable().scaledToFill()
                    } placeholder: {
                        DogAvatarPlaceholder(isSelected: isSelected)
                    }
                    .frame(width: 52, height: 52)
                    .clipShape(Circle())
                } else {
                    DogAvatarPlaceholder(isSelected: isSelected)
                }
            }

            VStack(alignment: .leading, spacing: 3) {
                HStack {
                    Text(dog.name)
                        .font(.headline)
                    
                    if isSelected {
                        Text("Active")
                            .font(.caption2.weight(.semibold))
                            .padding(.horizontal, 7)
                            .padding(.vertical, 2)
                            .background(Color.brandOrange.opacity(0.15))
                            .foregroundStyle(.brandOrange)
                            .clipShape(Capsule())
                    }
                }
                HStack(spacing: 8) {
                    if let breed = dog.breed {
                        Text(breed)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    
                    if let age = dog.age {
                        Text("·")
                            .foregroundStyle(.secondary)
                        Text("\(age) yr\(age == 1 ? "" : "s")")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    
                    if let weight = dog.weight {
                        Text("·")
                            .foregroundStyle(.secondary)
                        Text("\(String(format: "%.0f", weight)) lbs")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                }
                
                if !dog.allergies.isEmpty {
                    HStack(spacing: 4) {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .font(.caption2)
                            .foregroundStyle(.orange)
                        Text("\(dog.allergies.count) allerg\(dog.allergies.count == 1 ? "y" : "ies")")
                            .font(.caption)
                            .foregroundStyle(.orange)
                    }
                }
            }
        }
        .padding(.vertical, 4)
    }
}
