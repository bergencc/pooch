import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useAuth } from '../hooks/useAuth'
import { Dog, Eye, EyeOff, AlertCircle } from 'lucide-react'
import { getErrorMessage } from '../lib/utils'

const loginSchema = z.object({
    email: z.string().email('Invalid email address'),
    password: z.string().min(1, 'Password is required'),
})

type LoginForm = z.infer<typeof loginSchema>

export default function LoginPage() {
    const { login } = useAuth()
    const navigate = useNavigate()
    const [showPassword, setShowPassword] = useState(false)
    const [error, setError] = useState('')

    const {
        register,
        handleSubmit,
        formState: { errors, isSubmitting },
    } = useForm<LoginForm>({ resolver: zodResolver(loginSchema) })

    const onSubmit = async (data: LoginForm) => {
        setError('')

        try {
            await login(data.email, data.password)

            navigate('/dashboard')
        } catch (err) {
            setError(getErrorMessage(err))
        }
    }

    return (
        <div className="min-h-screen bg-gradient-to-br from-brand-50 to-orange-50 flex items-center justify-center p-4">
            <div className="w-full max-w-md">
                {/* Logo */}
                <div className="text-center mb-8">
                    <div className="inline-flex items-center justify-center w-16 h-16 bg-brand-500 rounded-2xl shadow-lg mb-4">
                        <Dog className="w-9 h-9 text-white" />
                    </div>
                    <h1 className="text-2xl font-bold text-gray-900">Pooch Scan</h1>
                    <p className="text-gray-500 mt-1">Admin Portal — Sign in to continue</p>
                </div>

                <div className="card p-8">
                    <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
                        {error && (
                            <div className="flex items-start gap-3 p-3 bg-red-50 border border-red-200 rounded-lg text-sm text-red-700">
                                <AlertCircle className="w-4 h-4 flex-shrink-0 mt-0.5" />
                                {error}
                            </div>
                        )}

                        <div>
                            <label className="label">Email address</label>
                            <input
                                {...register('email')}
                                type="email"
                                placeholder="admin@poochscan.com"
                                className="input"
                                autoComplete="email"
                            />
                            {errors.email && (
                                <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>
                            )}
                        </div>

                        <div>
                            <label className="label">Password</label>
                            <div className="relative">
                                <input
                                    {...register('password')}
                                    type={showPassword ? 'text' : 'password'}
                                    placeholder="••••••••"
                                    className="input pr-10"
                                    autoComplete="current-password"
                                />
                                <button
                                    type="button"
                                    onClick={() => setShowPassword(!showPassword)}
                                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                >
                                    {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                                </button>
                            </div>
                            {errors.password && (
                                <p className="mt-1 text-xs text-red-600">{errors.password.message}</p>
                            )}
                        </div>

                        <button type="submit" className="btn-primary w-full justify-center" disabled={isSubmitting}>
                            {isSubmitting ? (
                                <span className="flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  Signing in…
                </span>
                            ) : (
                                'Sign in'
                            )}
                        </button>
                    </form>

                    <p className="mt-4 text-center text-xs text-gray-400">
                        Admin access only — contact your system administrator for credentials
                    </p>
                </div>
            </div>
        </div>
    )
}
