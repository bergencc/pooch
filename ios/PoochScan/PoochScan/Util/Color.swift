import SwiftUI

extension Color {
    static let brandOrange = Color(red: 0.97, green: 0.49, blue: 0.17)
    static let brandOrangeDark = Color(red: 0.87, green: 0.33, blue: 0.07)
}
extension ShapeStyle where Self == Color {
    static var brandOrange: Color { Color.brandOrange }
    static var brandOrangeDark: Color { Color.brandOrangeDark }
}

