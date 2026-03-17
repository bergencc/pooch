import SwiftUI
import PhotosUI

struct DogDetailView: View {
    let dog: Dog
    @EnvironmentObject var dogsVM: DogsViewModel
    @State private var showEdit = false
    @State private var showDeleteAlert = false
    @State private var showPhotosPicker = false
    @State private var selectedPhotoItem: PhotosPickerItem?
    @Environment(\.dismiss) var dismiss

    var body: some View {
        ScrollView {
            VStack(spacing: 24) {
                // Photo & Name
                VStack(spacing: 16) {
                    ZStack {
                        if let urlStr = dog.photoUrl, let url = URL(string: urlStr) {
                            AsyncImage(url: url) { img in
                                img.resizable().scaledToFill()
                            } placeholder: {
                                Circle().fill(Color(.systemGray5))
                                Image(systemName: "pawprint.fill")
                                    .font(.system(size: 40))
                                    .foregroundStyle(.secondary)
                            }
                            .frame(width: 110, height: 110)
                            .clipShape(Circle())
                        } else {
                            Circle()
                                .fill(
                                    LinearGradient(colors: [.brandOrange.opacity(0.6), .brandOrangeDark],
                                                   startPoint: .topLeading, endPoint: .bottomTrailing)
                                )
                                .frame(width: 110, height: 110)
                            Image(systemName: "pawprint.fill")
                                .font(.system(size: 44))
                                .foregroundStyle(.white)
                        }

                        PhotosPicker(selection: $selectedPhotoItem, matching: .images) {
                            ZStack {
                                Circle().fill(.brandOrange)
                                Image(systemName: "camera.fill").foregroundStyle(.white).font(.caption)
                            }
                            .frame(width: 28, height: 28)
                        }
                        .offset(x: 36, y: 36)
                    }

                    Text(dog.name)
                        .font(.title.bold())
                    if let breed = dog.breed {
                        Text(breed)
                            .font(.subheadline)
                            .foregroundStyle(.secondary)
                    }

                    if dogsVM.selectedDog?.id != dog.id {
                        Button {
                            dogsVM.selectedDog = dog
                        } label: {
                            Label("Set as Active Dog", systemImage: "checkmark.circle.fill")
                                .font(.subheadline.weight(.medium))
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(.brandOrange)
                        .controlSize(.small)
                    } else {
                        Label("Active Dog", systemImage: "checkmark.circle.fill")
                            .font(.subheadline.weight(.medium))
                            .foregroundStyle(.brandOrange)
                    }
                }
                .padding(.top)

                // Stats Grid
                LazyVGrid(columns: [.init(.flexible()), .init(.flexible())], spacing: 12) {
                    StatTile(label: "Age", value: dog.age.map { "\($0) year\($0 == 1 ? "" : "s")" } ?? "—", icon: "calendar")
                    StatTile(label: "Weight", value: dog.weight.map { "\(String(format: "%.1f", $0)) lbs" } ?? "—", icon: "scalemass")
                    StatTile(label: "Activity", value: dog.activityLevel?.capitalized ?? "—", icon: "figure.run")
                    StatTile(label: "Conditions", value: dog.healthConditions.isEmpty ? "None" : "\(dog.healthConditions.count)", icon: "heart.fill")
                }
                .padding(.horizontal)

                // Allergies
                if !dog.allergies.isEmpty {
                    SectionCard(title: "Allergies ⚠️") {
                        FlowLayout(spacing: 8) {
                            ForEach(dog.allergies, id: \.self) { allergy in
                                Text(allergy)
                                    .font(.subheadline)
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 6)
                                    .background(Color.orange.opacity(0.12))
                                    .foregroundStyle(.orange)
                                    .clipShape(Capsule())
                            }
                        }
                    }
                    .padding(.horizontal)
                }

                // Health Conditions
                if !dog.healthConditions.isEmpty {
                    SectionCard(title: "Health Conditions") {
                        VStack(alignment: .leading, spacing: 8) {
                            ForEach(dog.healthConditions, id: \.self) { condition in
                                HStack(spacing: 8) {
                                    Image(systemName: "cross.circle.fill").foregroundStyle(.red)
                                    Text(condition).font(.subheadline)
                                }
                            }
                        }
                    }
                    .padding(.horizontal)
                }

                // Scan History
                ScanHistoryView(dog: dog)
                    .padding(.horizontal)

                Spacer(minLength: 40)
            }
        }
        .navigationTitle(dog.name)
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Menu {
                    Button { showEdit = true } label: {
                        Label("Edit Profile", systemImage: "pencil")
                    }
                    Button(role: .destructive) {
                        showDeleteAlert = true
                    } label: {
                        Label("Delete Dog", systemImage: "trash")
                    }
                } label: {
                    Image(systemName: "ellipsis.circle")
                }
            }
        }
        .sheet(isPresented: $showEdit) {
            AddEditDogView(mode: .edit(dog))
                .environmentObject(dogsVM)
        }
        .alert("Delete \(dog.name)?", isPresented: $showDeleteAlert) {
            Button("Delete", role: .destructive) {
                Task {
                    await dogsVM.deleteDog(id: dog.id)
                    dismiss()
                }
            }
            Button("Cancel", role: .cancel) {}
        } message: {
            Text("This will permanently delete this dog profile and all associated scan history.")
        }
        .onChange(of: selectedPhotoItem) { _, item in
            guard let item else { return }
            Task {
                if let data = try? await item.loadTransferable(type: Data.self) {
                    await dogsVM.uploadPhoto(dogId: dog.id, imageData: data)
                }
            }
        }
    }
}
