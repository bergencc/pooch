import SwiftUI

struct RecentScansSection: View {
    let dog: Dog
    @StateObject private var vm = ScanHistoryViewModel()

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("Recent Scans for \(dog.name)")
                    .font(.headline)
                Spacer()
                NavigationLink("See all") {
                    ScanHistoryView(dog: dog)
                }
                .font(.subheadline)
                .foregroundStyle(.brandOrange)
            }
            .padding(.horizontal)

            if vm.isLoading {
                ProgressView()
                    .frame(maxWidth: .infinity)
            } else if vm.records.isEmpty {
                Text("No scans yet. Try scanning a product!")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                    .frame(maxWidth: .infinity, alignment: .center)
                    .padding()
            } else {
                VStack(spacing: 8) {
                    ForEach(vm.records.prefix(3)) { record in
                        ScanRecordRow(record: record)
                    }
                }
                .padding(.horizontal)
            }
        }
        .task(id: dog.id) {
            await vm.loadHistory(dogId: dog.id, refresh: true)
        }
    }
}
