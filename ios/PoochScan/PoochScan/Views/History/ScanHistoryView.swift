import SwiftUI

struct ScanHistoryView: View {
    let dog: Dog
    @StateObject private var vm = ScanHistoryViewModel()

    var body: some View {
        NavigationStack {
            Group {
                if vm.isLoading && vm.records.isEmpty {
                    ProgressView("Loading scan history...")
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else if vm.records.isEmpty {
                    VStack(spacing: 16) {
                        Image(systemName: "clock.badge.questionmark")
                            .font(.system(size: 56))
                            .foregroundStyle(.secondary)
                        Text("No Scans Yet")
                            .font(.title3.bold())
                        Text("Scan a product to see history here")
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    List {
                        ForEach(vm.records) { record in
                            NavigationLink {
                                if let product = record.product {
                                    ProductDetailView(product: product, recommendation: record.recommendation)
                                }
                            } label: {
                                ScanRecordRow(record: record)
                            }
                            .onAppear {
                                if record.id == vm.records.last?.id {
                                    Task { await vm.loadMore(dogId: dog.id) }
                                }
                            }
                        }

                        if vm.isLoading && !vm.records.isEmpty {
                            HStack {
                                Spacer()
                                ProgressView()
                                Spacer()
                            }
                            .listRowSeparator(.hidden)
                        }
                    }
                    .listStyle(.plain)
                }
            }
            .navigationTitle("Scan History")
            .navigationBarTitleDisplayMode(.inline)
            .refreshable {
                await vm.loadHistory(dogId: dog.id, refresh: true)
            }
        }
        .task {
            if vm.records.isEmpty {
                await vm.loadHistory(dogId: dog.id, refresh: true)
            }
        }
    }
}
