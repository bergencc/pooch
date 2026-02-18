import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../lib/api'
import type {AdminStats} from '@/types'
import { Package, Users, Dog, ScanLine, Plus, ArrowRight } from 'lucide-react'

function StatCard(
    {
        label,
        value,
        icon: Icon,
        color,
    }: {
    label: string
    value: number | string
    icon: React.ElementType
    color: string
}) {
    return (
        <div className="card p-6">
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-sm text-gray-500">{label}</p>
                    <p className="text-3xl font-bold text-gray-900 mt-1">
                        {typeof value === 'number' ? value.toLocaleString() : value}
                    </p>
                </div>
                <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${color}`}>
                    <Icon className="w-6 h-6" />
                </div>
            </div>
        </div>
    )
}

export default function DashboardPage() {
    const [stats, setStats] = useState<AdminStats | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        api
            .get('/admin/stats')
            .then((res) => setStats(res.data))
            .catch(() => {})
            .finally(() => setLoading(false))
    }, [])

    return (
        <div className="max-w-5xl mx-auto space-y-8">
            {/* Header */}
            <div>
                <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
                <p className="text-gray-500 mt-1">Welcome to the Pooch Scan admin portal</p>
            </div>

            {/* Stats */}
            {loading ? (
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                    {[...Array(4)].map((_, i) => (
                        <div key={i} className="card p-6 animate-pulse">
                            <div className="h-4 w-24 bg-gray-200 rounded mb-3" />
                            <div className="h-8 w-16 bg-gray-200 rounded" />
                        </div>
                    ))}
                </div>
            ) : (
                <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
                    <StatCard
                        label="Total Products"
                        value={stats?.total_products ?? 0}
                        icon={Package}
                        color="bg-brand-100 text-brand-700"
                    />
                    <StatCard
                        label="Registered Users"
                        value={stats?.total_users ?? 0}
                        icon={Users}
                        color="bg-blue-100 text-blue-700"
                    />
                    <StatCard
                        label="Dog Profiles"
                        value={stats?.total_dogs ?? 0}
                        icon={Dog}
                        color="bg-purple-100 text-purple-700"
                    />
                    <StatCard
                        label="Total Scans"
                        value={stats?.total_scans ?? 0}
                        icon={ScanLine}
                        color="bg-green-100 text-green-700"
                    />
                </div>
            )}

            {/* Quick actions */}
            <div className="card p-6">
                <h2 className="font-semibold text-gray-900 mb-4">Quick Actions</h2>
                <div className="grid sm:grid-cols-2 gap-3">
                    <Link
                        to="/products/new"
                        className="flex items-center gap-4 p-4 border border-gray-200 rounded-xl hover:border-brand-300 hover:bg-brand-50 transition-colors group"
                    >
                        <div className="w-10 h-10 bg-brand-100 rounded-lg flex items-center justify-center group-hover:bg-brand-200 transition-colors">
                            <Plus className="w-5 h-5 text-brand-700" />
                        </div>
                        <div className="flex-1">
                            <p className="font-medium text-gray-900">Add New Product</p>
                            <p className="text-sm text-gray-500">Enter product details and ingredients</p>
                        </div>
                        <ArrowRight className="w-4 h-4 text-gray-400 group-hover:text-brand-600 transition-colors" />
                    </Link>

                    <Link
                        to="/products"
                        className="flex items-center gap-4 p-4 border border-gray-200 rounded-xl hover:border-brand-300 hover:bg-brand-50 transition-colors group"
                    >
                        <div className="w-10 h-10 bg-brand-100 rounded-lg flex items-center justify-center group-hover:bg-brand-200 transition-colors">
                            <Package className="w-5 h-5 text-brand-700" />
                        </div>
                        <div className="flex-1">
                            <p className="font-medium text-gray-900">Manage Products</p>
                            <p className="text-sm text-gray-500">View, edit, and delete products</p>
                        </div>
                        <ArrowRight className="w-4 h-4 text-gray-400 group-hover:text-brand-600 transition-colors" />
                    </Link>
                </div>
            </div>

            {/* Info */}
            <div className="card p-6 bg-brand-50 border-brand-200">
                <h2 className="font-semibold text-brand-900 mb-2">🐾 Getting Started</h2>
                <ul className="text-sm text-brand-800 space-y-1 list-disc list-inside">
                    <li>Add products by scanning or entering their barcode</li>
                    <li>Include complete ingredient lists for accurate health recommendations</li>
                    <li>Set eco scores (A–F) to help environmentally-conscious pet owners</li>
                    <li>The recommendation engine automatically flags harmful ingredients like xylitol, grapes, chocolate, and onions</li>
                </ul>
            </div>
        </div>
    )
}
