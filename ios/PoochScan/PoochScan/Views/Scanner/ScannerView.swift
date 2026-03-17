import SwiftUI

struct ScannerView: View {
    @EnvironmentObject var dogsVM: DogsViewModel
    @EnvironmentObject var scanVM: ScanViewModel
    @Environment(\.dismiss) var dismiss

    var body: some View {
        NavigationStack {
            ZStack {
                if dogsVM.selectedDog != nil {
                    ScannerContent()
                        .environmentObject(dogsVM)
                        .environmentObject(scanVM)
                } else {
                    VStack(spacing: 20) {
                        Image(systemName: "pawprint.circle")
                            .font(.system(size: 60))
                            .foregroundStyle(.brandOrange)
                        Text("No Dog Selected")
                            .font(.title2.bold())
                        Text("Please add a dog profile first to scan products and get personalized recommendations.")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                            .multilineTextAlignment(.center)
                            .padding(.horizontal, 40)
                        NavigationLink("Add a Dog") {
                            AddEditDogView(mode: .add)
                                .environmentObject(dogsVM)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(.brandOrange)
                    }
                }
            }
            .navigationTitle("Scanner")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}
