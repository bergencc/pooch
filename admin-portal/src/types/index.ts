export interface User {
    id: string
    email: string
    name: string | null
    is_admin: 'user' | 'admin'
    created_at: string
}

export interface AuthTokens {
    access_token: string
    token_type: string
    expires_in: number
}

export type ProductType = 'food' | 'treat' | 'medication' | 'supplement' | 'toy' | 'other'
export type EcoScore = 'A' | 'B' | 'C' | 'D' | 'E' | 'F'

export interface Product {
    id: string
    barcode: string
    name: string
    brand: string | null
    product_type: ProductType | null
    ingredients: string[]
    nutrition_info: Record<string, string> | null
    eco_score: EcoScore | null
    photo_url: string | null
    created_at: string
    updated_at: string
}

export interface ProductCreate {
    barcode: string
    name: string
    brand?: string
    product_type?: ProductType
    ingredients?: string[]
    nutrition_info?: Record<string, string>
    eco_score?: EcoScore
}

export interface ProductListResponse {
    items: Product[]
    total: number
    page: number
    per_page: number
    pages: number
}

export interface AdminStats {
    total_products: number
    total_users: number
    total_dogs: number
    total_scans: number
}

export interface ApiError {
    detail: string | { msg: string; loc: string[] }[]
}
