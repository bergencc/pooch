import SwiftUI

struct DogChip: View {
    let dog: Dog
    let isSelected: Bool
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 8) {
                ZStack {
                    Circle()
                        .fill(isSelected ? Color.brandOrange : Color(.systemGray5))
                        .frame(width: 32, height: 32)
                    if let url = dog.photoUrl, let imageURL = URL(string: url) {
                        AsyncImage(url: imageURL) { img in
                            img.resizable().scaledToFill()
                        } placeholder: {
                            Image(systemName: "pawprint.fill")
                                .foregroundStyle(isSelected ? .white : .secondary)
                                .font(.system(size: 14))
                        }
                        .frame(width: 32, height: 32)
                        .clipShape(Circle())
                    } else {
                        Image(systemName: "pawprint.fill")
                            .foregroundStyle(isSelected ? .white : .secondary)
                            .font(.system(size: 14))
                    }
                }
                Text(dog.name)
                    .font(.subheadline.weight(.medium))
                    .foregroundStyle(isSelected ? .brandOrange : .primary)
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
            .background(
                RoundedRectangle(cornerRadius: 20)
                    .fill(isSelected ? Color.brandOrange.opacity(0.12) : Color(.systemGray6))
                    .overlay(
                        RoundedRectangle(cornerRadius: 20)
                            .stroke(isSelected ? Color.brandOrange : .clear, lineWidth: 1.5)
                    )
            )
        }
        .buttonStyle(.plain)
    }
}
