import SwiftUI

struct DogAvatarPlaceholder: View {
    let isSelected: Bool
    
    var body: some View {
        Image(systemName: "pawprint.fill")
            .font(.system(size: 22))
            .foregroundStyle(isSelected ? .white : .secondary)
    }
}
