import SwiftUI

struct ScannerContent: View {
    @EnvironmentObject var dogsVM: DogsViewModel
    @EnvironmentObject var scanVM: ScanViewModel

    var body: some View {
        ZStack {
            BarcodeScannerRepresentable { barcode in
                if let dog = dogsVM.selectedDog {
                    Task {
                        await scanVM.processBarcode(barcode, dogId: dog.id)
                    }
                }
            }
            .ignoresSafeArea()

            // Overlay UI
            VStack {
                // Dog badge
                if let dog = dogsVM.selectedDog {
                    HStack(spacing: 8) {
                        Image(systemName: "pawprint.fill")
                            .font(.caption)
                        Text("Scanning for \(dog.name)")
                            .font(.subheadline.weight(.medium))
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                    .background(.ultraThinMaterial, in: Capsule())
                    .padding(.top)
                }

                Spacer()

                // Viewfinder frame
                RoundedRectangle(cornerRadius: 16)
                    .stroke(Color.brandOrange, lineWidth: 3)
                    .frame(width: 260, height: 160)
                    .overlay {
                        if scanVM.isLoading {
                            ProgressView()
                                .tint(.brandOrange)
                                .scaleEffect(1.5)
                        }
                    }

                Text("Point at a product barcode")
                    .font(.caption)
                    .foregroundStyle(.white)
                    .padding(.top, 12)

                Spacer()

                // Error
                if let error = scanVM.errorMessage {
                    HStack(spacing: 8) {
                        Image(systemName: "xmark.circle.fill")
                        Text(error)
                            .font(.subheadline)
                    }
                    .foregroundStyle(.white)
                    .padding()
                    .background(.red.opacity(0.85), in: RoundedRectangle(cornerRadius: 12))
                    .padding(.horizontal, 24)
                    .padding(.bottom, 8)
                }

                // Reset button when there's an error or last result
                if scanVM.errorMessage != nil || scanVM.lastBarcode != nil {
                    Button {
                        scanVM.reset()
                    } label: {
                        Label("Scan Again", systemImage: "arrow.counterclockwise")
                            .font(.subheadline.weight(.medium))
                            .padding(.horizontal, 20)
                            .padding(.vertical, 10)
                            .background(.ultraThinMaterial, in: Capsule())
                    }
                    .foregroundStyle(.white)
                    .padding(.bottom)
                }
            }
        }
        .sheet(isPresented: $scanVM.showResult) {
            if let product = scanVM.scannedProduct {
                ProductDetailView(product: product, recommendation: scanVM.recommendation)
                    .onDisappear { scanVM.reset() }
            }
        }
    }
}
