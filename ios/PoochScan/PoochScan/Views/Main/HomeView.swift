import SwiftUI

struct HomeView: View {
    @EnvironmentObject var dogsVM: DogsViewModel
    @EnvironmentObject var scanVM: ScanViewModel
    @EnvironmentObject var authVM: AuthViewModel

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    // Greeting
                    VStack(alignment: .leading, spacing: 4) {
                        Text("Hello, \(authVM.currentUser?.name ?? "there")! 👋")
                            .font(.title2.bold())
                        Text("Ready to scan some products?")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    .padding(.horizontal)

                    // Active Dog Picker
                    if !dogsVM.dogs.isEmpty {
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Scanning for")
                                .font(.headline)
                                .padding(.horizontal)

                            ScrollView(.horizontal, showsIndicators: false) {
                                HStack(spacing: 12) {
                                    ForEach(dogsVM.dogs) { dog in
                                        DogChip(
                                            dog: dog,
                                            isSelected: dogsVM.selectedDog?.id == dog.id
                                        ) {
                                            dogsVM.selectedDog = dog
                                        }
                                    }
                                }
                                .padding(.horizontal)
                            }
                        }
                    } else {
                        NoDogsBanner()
                            .padding(.horizontal)
                    }

                    // Quick Scan CTA
                    NavigationLink {
                        ScannerView()
                            .environmentObject(dogsVM)
                            .environmentObject(scanVM)
                    } label: {
                        HStack {
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Scan a Product")
                                    .font(.title3.bold())
                                    .foregroundStyle(.white)
                                Text("Tap to open the barcode scanner")
                                    .font(.subheadline)
                                    .foregroundStyle(.white.opacity(0.85))
                            }
                            Spacer()
                            Image(systemName: "barcode.viewfinder")
                                .font(.system(size: 40))
                                .foregroundStyle(.white.opacity(0.9))
                        }
                        .padding(20)
                        .background(
                            LinearGradient(
                                colors: [.brandOrange, .brandOrangeDark],
                                startPoint: .topLeading,
                                endPoint: .bottomTrailing
                            )
                        )
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                    }
                    .padding(.horizontal)

                    // Recent Scans
                    if let dog = dogsVM.selectedDog {
                        RecentScansSection(dog: dog)
                    }

                    Spacer(minLength: 40)
                }
                .padding(.top)
            }
            .navigationTitle("Pooch Scan")
            .navigationBarTitleDisplayMode(.large)
        }
    }
}
