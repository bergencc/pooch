import SwiftUI
import Combine

@MainActor
class DogsViewModel: ObservableObject {
    @Published var dogs: [Dog] = []
    @Published var selectedDog: Dog?
    @Published var isLoading = false
    @Published var errorMessage: String?
    @Published var successMessage: String?

    private let api = APIClient.shared

    func loadDogs() async {
        isLoading = true
        errorMessage = nil
        
        do {
            dogs = try await api.getDogs()
            
            if selectedDog == nil, let first = dogs.first {
                selectedDog = first
            } else if let selected = selectedDog,
                      let updated = dogs.first(where: { $0.id == selected.id }) {
                selectedDog = updated
            }
        } catch {
            errorMessage = error.localizedDescription
        }
        
        isLoading = false
    }

    func createDog(_ request: DogCreateRequest) async throws -> Dog {
        let dog = try await api.createDog(request)
        
        dogs.append(dog)
        
        if selectedDog == nil { selectedDog = dog }
        
        return dog
    }

    func updateDog(id: String, _ request: DogCreateRequest) async throws -> Dog {
        let dog = try await api.updateDog(id: id, request)
        
        if let idx = dogs.firstIndex(where: { $0.id == id }) {
            dogs[idx] = dog
        }
        
        if selectedDog?.id == id { selectedDog = dog }
        
        return dog
    }

    func deleteDog(id: String) async {
        do {
            try await api.deleteDog(id: id)
            
            dogs.removeAll { $0.id == id }
            
            if selectedDog?.id == id {
                selectedDog = dogs.first
            }
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    func uploadPhoto(dogId: String, imageData: Data) async {
        do {
            let updatedDog = try await api.uploadDogPhoto(dogId: dogId, imageData: imageData)
            
            if let idx = dogs.firstIndex(where: { $0.id == dogId }) {
                dogs[idx] = updatedDog
            }
            
            if selectedDog?.id == dogId { selectedDog = updatedDog }
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
