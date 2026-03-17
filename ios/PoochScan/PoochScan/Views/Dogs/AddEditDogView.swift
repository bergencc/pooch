import SwiftUI
import PhotosUI

struct AddEditDogView: View {
    enum Mode {
        case add
        case edit(Dog)
    }

    let mode: Mode
    @EnvironmentObject var dogsVM: DogsViewModel
    @Environment(\.dismiss) var dismiss

    @State private var name = ""
    @State private var breed = ""
    @State private var ageText = ""
    @State private var weightText = ""
    @State private var activityLevel = "medium"
    @State private var allergyInput = ""
    @State private var allergies: [String] = []
    @State private var conditionInput = ""
    @State private var healthConditions: [String] = []
    @State private var isLoading = false
    @State private var errorMessage: String?

    let activityLevels = ["low", "medium", "high"]

    var editingDog: Dog? {
        if case .edit(let dog) = mode { return dog }
        
        return nil
    }

    var title: String {
        switch mode {
        case .add: return "Add Dog"
        case .edit(let dog): return "Edit \(dog.name)"
        }
    }

    var body: some View {
        NavigationStack {
            Form {
                Section("Basic Info") {
                    LabeledContent("Name") {
                        TextField("Required", text: $name)
                            .multilineTextAlignment(.trailing)
                    }
                    LabeledContent("Breed") {
                        TextField("Optional", text: $breed)
                            .multilineTextAlignment(.trailing)
                    }
                    LabeledContent("Age (years)") {
                        TextField("Optional", text: $ageText)
                            .keyboardType(.numberPad)
                            .multilineTextAlignment(.trailing)
                    }
                    LabeledContent("Weight (lbs)") {
                        TextField("Optional", text: $weightText)
                            .keyboardType(.decimalPad)
                            .multilineTextAlignment(.trailing)
                    }
                }

                Section("Activity Level") {
                    Picker("Activity", selection: $activityLevel) {
                        ForEach(activityLevels, id: \.self) { level in
                            Text(level.capitalized).tag(level)
                        }
                    }
                    .pickerStyle(.segmented)
                }

                Section("Allergies") {
                    if !allergies.isEmpty {
                        FlowLayout(spacing: 6) {
                            ForEach(allergies, id: \.self) { a in
                                HStack(spacing: 4) {
                                    Text(a).font(.subheadline)
                                    Button {
                                        allergies.removeAll { $0 == a }
                                    } label: {
                                        Image(systemName: "xmark").font(.caption2)
                                    }
                                }
                                .padding(.horizontal, 10)
                                .padding(.vertical, 5)
                                .background(Color.orange.opacity(0.12))
                                .foregroundStyle(.orange)
                                .clipShape(Capsule())
                            }
                        }
                        .padding(.vertical, 4)
                    }
                    HStack {
                        TextField("Add allergy (e.g. chicken)", text: $allergyInput)
                        Button("Add") {
                            let trimmed = allergyInput.trimmingCharacters(in: .whitespaces)
                            
                            if !trimmed.isEmpty && !allergies.contains(trimmed) {
                                allergies.append(trimmed)
                                allergyInput = ""
                            }
                        }
                        .disabled(allergyInput.trimmingCharacters(in: .whitespaces).isEmpty)
                    }
                }

                Section("Health Conditions") {
                    if !healthConditions.isEmpty {
                        ForEach(healthConditions, id: \.self) { condition in
                            Text(condition)
                        }
                        .onDelete { idx in
                            healthConditions.remove(atOffsets: idx)
                        }
                    }
                    
                    HStack {
                        TextField("Add condition (e.g. diabetes)", text: $conditionInput)
                        Button("Add") {
                            let trimmed = conditionInput.trimmingCharacters(in: .whitespaces)
                            
                            if !trimmed.isEmpty && !healthConditions.contains(trimmed) {
                                healthConditions.append(trimmed)
                                conditionInput = ""
                            }
                        }
                        .disabled(conditionInput.trimmingCharacters(in: .whitespaces).isEmpty)
                    }
                }

                if let error = errorMessage {
                    Section {
                        Text(error)
                            .foregroundStyle(.red)
                            .font(.caption)
                    }
                }
            }
            .navigationTitle(title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button("Cancel") { dismiss() }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button(editingDog == nil ? "Add" : "Save") {
                        Task { await save() }
                    }
                    .fontWeight(.semibold)
                    .disabled(name.trimmingCharacters(in: .whitespaces).isEmpty || isLoading)
                    .overlay {
                        if isLoading { ProgressView().scaleEffect(0.8) }
                    }
                }
            }
            .onAppear {
                if let dog = editingDog {
                    name = dog.name
                    breed = dog.breed ?? ""
                    ageText = dog.age.map(String.init) ?? ""
                    weightText = dog.weight.map { String(format: "%.1f", $0) } ?? ""
                    activityLevel = dog.activityLevel ?? "medium"
                    allergies = dog.allergies
                    healthConditions = dog.healthConditions
                }
            }
        }
    }

    func save() async {
        isLoading = true
        errorMessage = nil
        
        let request = DogCreateRequest(
            name: name.trimmingCharacters(in: .whitespaces),
            breed: breed.isEmpty ? nil : breed,
            age: Int(ageText),
            weight: Double(weightText),
            activityLevel: activityLevel,
            allergies: allergies,
            healthConditions: healthConditions
        )
        
        do {
            if let dog = editingDog {
                _ = try await dogsVM.updateDog(id: dog.id, request)
            } else {
                _ = try await dogsVM.createDog(request)
            }
            
            dismiss()
        } catch {
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}
