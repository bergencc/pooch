import SwiftUI

struct AboutView: View {
    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                VStack(spacing: 12) {
                    ZStack {
                        Circle()
                            .fill(
                                LinearGradient(colors: [.brandOrange, .brandOrangeDark],
                                               startPoint: .topLeading, endPoint: .bottomTrailing)
                            )
                            .frame(width: 90, height: 90)
                        Image(systemName: "pawprint.fill")
                            .font(.system(size: 40))
                            .foregroundStyle(.white)
                    }
                    Text("Pooch Scan")
                        .font(.title.bold())
                    Text("Version 1.0.0 MVP")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                }
                .padding(.top, 32)

                VStack(alignment: .leading, spacing: 16) {
                    AboutSection(
                        icon: "shield.checkered",
                        title: "Health-First Scanning",
                        text: "Every product scan checks against 12+ dangerous ingredients including xylitol, chocolate, grapes, and more — tailored to your dog's specific allergies and health conditions."
                    )
                    AboutSection(
                        icon: "leaf.fill",
                        title: "Eco Scoring",
                        text: "Products are rated A–F for environmental sustainability, helping you make greener choices for your pup and the planet."
                    )
                    AboutSection(
                        icon: "pawprint.circle.fill",
                        title: "Multi-Dog Support",
                        text: "Manage multiple dog profiles, each with their own allergies, health conditions, and scan history."
                    )
                    AboutSection(
                        icon: "clock.fill",
                        title: "Scan History",
                        text: "Every scan is logged so you can track what products you've checked and their recommendations over time."
                    )
                }
                .padding(.horizontal, 24)

                Spacer(minLength: 40)
            }
        }
        .navigationTitle("About")
        .navigationBarTitleDisplayMode(.inline)
    }
}
