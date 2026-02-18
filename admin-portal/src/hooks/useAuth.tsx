import React, { createContext, useContext, useState, useEffect, type ReactNode } from 'react'
import api from '../lib/api'
import type {User} from '@/types'

interface AuthContextType {
    user: User | null
    loading: boolean
    login: (email: string, password: string) => Promise<void>
    logout: () => void
}

const AuthContext = createContext<AuthContextType | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
    const [user, setUser] = useState<User | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        const token = localStorage.getItem('access_token')

        if (token) {
            api.get('/auth/me')
                .then((res) => setUser(res.data))
                .catch(() => localStorage.removeItem('access_token'))
                .finally(() => setLoading(false))
        } else {
            setLoading(false)
        }
    }, [])

    const login = async (email: string, password: string) => {
        const res = await api.post('/auth/login', { email, password })
        const { access_token } = res.data

        localStorage.setItem('access_token', access_token)

        const me = await api.get('/auth/me')

        if (me.data.is_admin !== 'admin') {
            localStorage.removeItem('access_token')

            throw new Error('Admin access required')
        }

        setUser(me.data)
    }

    const logout = () => {
        localStorage.removeItem('access_token')

        setUser(null)
    }

    return (
        <AuthContext.Provider value={{ user, loading, login, logout }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const ctx = useContext(AuthContext)

    if (!ctx) throw new Error('useAuth must be used inside AuthProvider')

    return ctx
}
