import Foundation
import Combine

@MainActor
class APIClient: ObservableObject {
    static let shared = APIClient()

    private var baseURL: String {
        // TODO: Update this to the server's IP/hostname
        return UserDefaults.standard.string(forKey: "serverURL") ?? "http://localhost:8000"
    }

    var token: String? {
        get { UserDefaults.standard.string(forKey: "authToken") }
        
        set {
            if let value = newValue {
                UserDefaults.standard.set(value, forKey: "authToken")
            } else {
                UserDefaults.standard.removeObject(forKey: "authToken")
            }
        }
    }

    private func makeURL(_ path: String) throws -> URL {
        guard let url = URL(string: baseURL + "/api/v1" + path) else {
            throw APIError.invalidURL
        }
        
        return url
    }

    private func makeRequest(_ url: URL, method: String = "GET", body: Data? = nil, requiresAuth: Bool = true) -> URLRequest {
        var request = URLRequest(url: url)
        
        request.httpMethod = method
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        if requiresAuth, let token = token {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        request.httpBody = body
        
        return request
    }

    private func perform<T: Decodable>(_ request: URLRequest) async throws -> T {
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw APIError.networkError("Invalid response")
        }
        
        switch httpResponse.statusCode {
        case 200...299:
            do {
                let decoder = JSONDecoder()
                
                return try decoder.decode(T.self, from: data)
            } catch {
                throw APIError.decodingError(error.localizedDescription)
            }
        case 401:
            token = nil
            
            throw APIError.unauthorized
        default:
            let msg = (try? JSONDecoder().decode(ServerErrorResponse.self, from: data))?.detail ?? "Unknown error"
            
            throw APIError.serverError(httpResponse.statusCode, msg)
        }
    }

    // MARK: - Auth
    func login(email: String, password: String) async throws -> AuthResponse {
        let url = try makeURL("/auth/login")
        let body = try JSONEncoder().encode(LoginRequest(email: email, password: password))
        let req = makeRequest(url, method: "POST", body: body, requiresAuth: false)
        
        return try await perform(req)
    }

    func register(email: String, password: String, name: String) async throws -> AuthResponse {
        let url = try makeURL("/auth/register")
        let body = try JSONEncoder().encode(RegisterRequest(email: email, password: password, name: name))
        let req = makeRequest(url, method: "POST", body: body, requiresAuth: false)
        
        return try await perform(req)
    }

    func getMe() async throws -> User {
        let url = try makeURL("/auth/me")
        let req = makeRequest(url)
        
        return try await perform(req)
    }

    // MARK: - Dogs
    func getDogs() async throws -> [Dog] {
        let url = try makeURL("/dogs")
        let req = makeRequest(url)
        
        return try await perform(req)
    }

    func getDog(id: String) async throws -> Dog {
        let url = try makeURL("/dogs/\(id)")
        let req = makeRequest(url)
        
        return try await perform(req)
    }

    func createDog(_ dog: DogCreateRequest) async throws -> Dog {
        let url = try makeURL("/dogs")
        let body = try JSONEncoder().encode(dog)
        let req = makeRequest(url, method: "POST", body: body)
        
        return try await perform(req)
    }

    func updateDog(id: String, _ dog: DogCreateRequest) async throws -> Dog {
        let url = try makeURL("/dogs/\(id)")
        let body = try JSONEncoder().encode(dog)
        let req = makeRequest(url, method: "PUT", body: body)
        
        return try await perform(req)
    }

    func deleteDog(id: String) async throws {
        let url = try makeURL("/dogs/\(id)")
        let req = makeRequest(url, method: "DELETE")
        let (_, response) = try await URLSession.shared.data(for: req)
        
        guard let httpResponse = response as? HTTPURLResponse, httpResponse.statusCode == 200 else {
            throw APIError.serverError(0, "Delete failed")
        }
    }

    func uploadDogPhoto(dogId: String, imageData: Data) async throws -> Dog {
        let url = try makeURL("/dogs/\(dogId)/photo")
        var request = URLRequest(url: url)
        
        request.httpMethod = "POST"
        
        let boundary = UUID().uuidString
        
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        if let token = token {
            request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }
        
        var body = Data()
        
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"photo.jpg\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: image/jpeg\r\n\r\n".data(using: .utf8)!)
        body.append(imageData)
        body.append("\r\n--\(boundary)--\r\n".data(using: .utf8)!)
        
        request.httpBody = body
        
        return try await perform(request)
    }

    // MARK: - Scans
    func scan(barcode: String, dogId: String) async throws -> ScanResponse {
        let url = try makeURL("/scans")
        let body = try JSONEncoder().encode(["barcode": barcode, "dog_id": dogId])
        let req = makeRequest(url, method: "POST", body: body)
        
        return try await perform(req)
    }

    func getScanHistory(dogId: String, page: Int = 1) async throws -> PaginatedResponse<ScanRecord> {
        let url = try makeURL("/scans/\(dogId)?page=\(page)&page_size=20")
        let req = makeRequest(url)
        
        return try await perform(req)
    }

    func getProductByBarcode(_ barcode: String) async throws -> Product {
        let url = try makeURL("/scans/products/\(barcode)")
        let req = makeRequest(url)
        
        return try await perform(req)
    }
}
