import SwiftUI

struct ProductDetailView: View {
    let product: Product
    let recommendation: String?
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    // Header / Image
                    ZStack(alignment: .bottomLeading) {
                        if let urlStr = product.photoUrl, let url = URL(string: urlStr) {
                            AsyncImage(url: url) { img in
                                img.resizable().scaledToFill()
                            } placeholder: {
                                ProductImagePlaceholder(type: product.productType)
                            }
                            .frame(height: 220)
                            .clipped()
                        } else {
                            ProductImagePlaceholder(type: product.productType)
                                .frame(height: 220)
                        }

                        LinearGradient(
                            colors: [.clear, .black.opacity(0.7)],
                            startPoint: .center,
                            endPoint: .bottom
                        )

                        VStack(alignment: .leading, spacing: 4) {
                            if let brand = product.brand {
                                Text(brand.uppercased())
                                    .font(.caption.weight(.bold))
                                    .foregroundStyle(.white.opacity(0.8))
                                    .tracking(1.2)
                            }
                            Text(product.name)
                                .font(.title3.bold())
                                .foregroundStyle(.white)
                        }
                        .padding(16)
                    }
                    .frame(height: 220)

                    VStack(spacing: 20) {
                        // Badges Row
                        HStack(spacing: 12) {
                            if product.productType != nil {
                                Badge(label: product.productTypeDisplay, color: .blue)
                            }
                            
                            if let eco = product.ecoScore {
                                EcoScoreBadge(score: eco)
                            }
                            
                            Spacer()
                            Text(product.barcode)
                                .font(.caption.monospaced())
                                .foregroundStyle(.secondary)
                        }

                        // Recommendation
                        if let rec = recommendation {
                            RecommendationCard(text: rec)
                        }

                        // Ingredients
                        if !product.ingredients.isEmpty {
                            SectionCard(title: "Ingredients (\(product.ingredients.count))") {
                                IngredientsList(ingredients: product.ingredients)
                            }
                        }

                        // Nutrition
                        if let nutrition = product.nutritionInfo, !nutrition.isEmpty {
                            SectionCard(title: "Nutrition Facts") {
                                NutritionGrid(facts: nutrition)
                            }
                        }

                        Spacer(minLength: 40)
                    }
                    .padding(20)
                }
            }
            .ignoresSafeArea(edges: .top)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button { dismiss() } label: {
                        Image(systemName: "xmark.circle.fill")
                            .symbolRenderingMode(.hierarchical)
                            .foregroundStyle(.white)
                            .font(.title3)
                    }
                }
            }
        }
    }
}
