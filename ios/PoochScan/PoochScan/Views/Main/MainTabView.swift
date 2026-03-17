import SwiftUI

struct MainTabView: View {
    @StateObject var dogsVM = DogsViewModel()
    @StateObject var scanVM = ScanViewModel()
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            HomeView()
                .environmentObject(dogsVM)
                .environmentObject(scanVM)
                .tabItem {
                    Label("Home", systemImage: "house.fill")
                }
                .tag(0)

            ScannerView()
                .environmentObject(dogsVM)
                .environmentObject(scanVM)
                .tabItem {
                    Label("Scan", systemImage: "barcode.viewfinder")
                }
                .tag(1)

            DogListView()
                .environmentObject(dogsVM)
                .tabItem {
                    Label("My Dogs", systemImage: "pawprint.fill")
                }
                .tag(2)

            ProfileView()
                .tabItem {
                    Label("Profile", systemImage: "person.fill")
                }
                .tag(3)
        }
        .accentColor(.brandOrange)
        .task {
            await dogsVM.loadDogs()
        }
    }
}
