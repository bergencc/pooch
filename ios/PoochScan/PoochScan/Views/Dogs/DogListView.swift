import SwiftUI
import PhotosUI

struct DogListView: View {
    @EnvironmentObject var dogsVM: DogsViewModel
    @State private var showAddDog = false

    var body: some View {
        NavigationStack {
            Group {
                if dogsVM.dogs.isEmpty && !dogsVM.isLoading {
                    EmptyDogsView(showAddDog: $showAddDog)
                } else {
                    List {
                        ForEach(dogsVM.dogs) { dog in
                            NavigationLink {
                                DogDetailView(dog: dog)
                                    .environmentObject(dogsVM)
                            } label: {
                                DogRow(dog: dog, isSelected: dogsVM.selectedDog?.id == dog.id)
                            }
                            .swipeActions(edge: .leading) {
                                Button {
                                    dogsVM.selectedDog = dog
                                } label: {
                                    Label("Select", systemImage: "checkmark.circle.fill")
                                }
                                .tint(.brandOrange)
                            }
                            .swipeActions(edge: .trailing) {
                                Button(role: .destructive) {
                                    Task { await dogsVM.deleteDog(id: dog.id) }
                                } label: {
                                    Label("Delete", systemImage: "trash")
                                }
                            }
                        }
                    }
                }
            }
            .navigationTitle("My Dogs")
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button {
                        showAddDog = true
                    } label: {
                        Image(systemName: "plus")
                    }
                }
            }
            .overlay {
                if dogsVM.isLoading && dogsVM.dogs.isEmpty {
                    ProgressView("Loading your dogs...")
                }
            }
            .refreshable {
                await dogsVM.loadDogs()
            }
            .sheet(isPresented: $showAddDog) {
                AddEditDogView(mode: .add)
                    .environmentObject(dogsVM)
            }
        }
    }
}
