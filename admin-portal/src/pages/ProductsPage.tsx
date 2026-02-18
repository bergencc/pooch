import { useEffect, useState, useCallback } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import api from '@/lib/api'
import type {Product, ProductListResponse} from '@/types'
import {
    Plus, Search, Edit2, Trash2, ChevronLeft, ChevronRight,
    Package, AlertCircle, Image, RefreshCw
} from 'lucide-react'
import { cn, formatDate, getErrorMessage, ECO_SCORE_COLORS, PRODUCT_TYPE_LABELS } from '@/lib/utils'

const PRODUCT_TYPES = ['', 'food', 'treat', 'medication', 'supplement', 'toy', 'other'] as const

export default function ProductsPage() {
    const [searchParams, setSearchParams] = useSearchParams()
    const [data, setData] = useState<ProductListResponse | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [deleting, setDeleting] = useState<string | null>(null)
    const [confirmDelete, setConfirmDelete] = useState<Product | null>(null)

    const page = Number(searchParams.get('page') ?? '1')
    const search = searchParams.get('search') ?? ''
    const productType = searchParams.get('type') ?? ''

    const fetchProducts = useCallback(async () => {
        setLoading(true)
        setError('')

        try {
            const params: Record<string, string> = { page: String(page), per_page: '20' }

            if (search) params.search = search

            if (productType) params.product_type = productType

            const res = await api.get('/admin/products', { params })

            setData(res.data)
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setLoading(false)
        }
    }, [page, search, productType])

    useEffect(() => { fetchProducts() }, [fetchProducts])

    const handleDelete = async (product: Product) => {
        setDeleting(product.id)
        setConfirmDelete(null)

        try {
            await api.delete(`/admin/products/${product.id}`)

            fetchProducts()
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setDeleting(null)
        }
    }

    const updateParam = (key: string, value: string) => {
        const next = new URLSearchParams(searchParams)

        if (value) next.set(key, value); else next.delete(key)

        if (key !== 'page') next.delete('page')

        setSearchParams(next)
    }

    return (
        <div className="max-w-6xl mx-auto space-y-6">
            {/* Header */}
            <div className="flex items-center justify-between">
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">Products</h1>
                    <p className="text-gray-500 mt-0.5">
                        {data ? `${data.total.toLocaleString()} products in database` : 'Loading…'}
                    </p>
                </div>
                <Link to="/products/new" className="btn-primary">
                    <Plus className="w-4 h-4" />
                    Add Product
                </Link>
            </div>

            {/* Filters */}
            <div className="card p-4 flex flex-col sm:flex-row gap-3">
                <div className="relative flex-1">
                    <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
                    <input
                        type="text"
                        placeholder="Search by name, brand, or barcode…"
                        defaultValue={search}
                        onChange={(e) => {
                            const v = e.target.value
                            const timer = setTimeout(() => updateParam('search', v), 400)

                            return () => clearTimeout(timer)
                        }}
                        className="input pl-9"
                    />
                </div>
                <select
                    value={productType}
                    onChange={(e) => updateParam('type', e.target.value)}
                    className="input sm:w-44"
                >
                    <option value="">All types</option>
                    {PRODUCT_TYPES.filter(Boolean).map((t) => (
                        <option key={t} value={t}>{PRODUCT_TYPE_LABELS[t] ?? t}</option>
                    ))}
                </select>
                <button onClick={fetchProducts} className="btn-secondary">
                    <RefreshCw className="w-4 h-4" />
                </button>
            </div>

            {/* Error */}
            {error && (
                <div className="flex items-center gap-3 p-4 bg-red-50 border border-red-200 rounded-xl text-red-700">
                    <AlertCircle className="w-5 h-5 flex-shrink-0" />
                    {error}
                </div>
            )}

            {/* Table */}
            <div className="card overflow-hidden">
                {loading ? (
                    <div className="p-12 text-center">
                        <div className="w-8 h-8 border-3 border-brand-500 border-t-transparent rounded-full animate-spin mx-auto" />
                    </div>
                ) : data?.items.length === 0 ? (
                    <div className="p-16 text-center">
                        <Package className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                        <p className="text-gray-500 font-medium">No products found</p>
                        <p className="text-sm text-gray-400 mt-1">
                            {search || productType ? 'Try adjusting your filters' : 'Add your first product to get started'}
                        </p>
                        {!search && !productType && (
                            <Link to="/products/new" className="btn-primary mt-4">
                                <Plus className="w-4 h-4" />
                                Add Product
                            </Link>
                        )}
                    </div>
                ) : (
                    <div className="overflow-x-auto">
                        <table className="w-full text-sm">
                            <thead className="bg-gray-50 border-b border-gray-200">
                            <tr>
                                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Product</th>
                                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Barcode</th>
                                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Type</th>
                                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Eco</th>
                                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Ingredients</th>
                                <th className="text-left px-4 py-3 text-xs font-semibold text-gray-500 uppercase tracking-wide">Added</th>
                                <th className="px-4 py-3" />
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                            {data?.items.map((product) => (
                                <tr key={product.id} className="hover:bg-gray-50 transition-colors">
                                    <td className="px-4 py-3">
                                        <div className="flex items-center gap-3">
                                            {product.photo_url ? (
                                                <img
                                                    src={product.photo_url}
                                                    alt={product.name}
                                                    className="w-9 h-9 rounded-lg object-cover border border-gray-200 flex-shrink-0"
                                                />
                                            ) : (
                                                <div className="w-9 h-9 bg-gray-100 rounded-lg flex items-center justify-center flex-shrink-0">
                                                    <Image className="w-4 h-4 text-gray-400" />
                                                </div>
                                            )}
                                            <div className="min-w-0">
                                                <p className="font-medium text-gray-900 truncate max-w-48">{product.name}</p>
                                                {product.brand && <p className="text-xs text-gray-500">{product.brand}</p>}
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-4 py-3">
                                        <code className="text-xs bg-gray-100 px-2 py-0.5 rounded font-mono">{product.barcode}</code>
                                    </td>
                                    <td className="px-4 py-3">
                                        {product.product_type ? (
                                            <span className="inline-flex items-center px-2 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-700 capitalize">
                          {PRODUCT_TYPE_LABELS[product.product_type] ?? product.product_type}
                        </span>
                                        ) : (
                                            <span className="text-gray-400">—</span>
                                        )}
                                    </td>
                                    <td className="px-4 py-3">
                                        {product.eco_score ? (
                                            <span className={cn('inline-flex items-center px-2 py-0.5 rounded-full text-xs font-bold', ECO_SCORE_COLORS[product.eco_score])}>
                          {product.eco_score}
                        </span>
                                        ) : (
                                            <span className="text-gray-400">—</span>
                                        )}
                                    </td>
                                    <td className="px-4 py-3">
                                        <span className="text-gray-500">{product.ingredients.length} items</span>
                                    </td>
                                    <td className="px-4 py-3 text-gray-500 text-xs">{formatDate(product.created_at)}</td>
                                    <td className="px-4 py-3">
                                        <div className="flex items-center gap-1 justify-end">
                                            <Link
                                                to={`/products/${product.id}/edit`}
                                                className="p-1.5 rounded-lg text-gray-400 hover:text-brand-600 hover:bg-brand-50 transition-colors"
                                            >
                                                <Edit2 className="w-4 h-4" />
                                            </Link>
                                            <button
                                                onClick={() => setConfirmDelete(product)}
                                                disabled={deleting === product.id}
                                                className="p-1.5 rounded-lg text-gray-400 hover:text-red-600 hover:bg-red-50 transition-colors disabled:opacity-50"
                                            >
                                                {deleting === product.id ? (
                                                    <span className="w-4 h-4 block border-2 border-red-400 border-t-transparent rounded-full animate-spin" />
                                                ) : (
                                                    <Trash2 className="w-4 h-4" />
                                                )}
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}

                {/* Pagination */}
                {data && data.pages > 1 && (
                    <div className="flex items-center justify-between px-4 py-3 border-t border-gray-200">
                        <p className="text-sm text-gray-500">
                            Showing {(page - 1) * 20 + 1}–{Math.min(page * 20, data.total)} of {data.total}
                        </p>
                        <div className="flex items-center gap-2">
                            <button
                                onClick={() => updateParam('page', String(page - 1))}
                                disabled={page <= 1}
                                className="btn-secondary py-1.5 px-2.5 disabled:opacity-40"
                            >
                                <ChevronLeft className="w-4 h-4" />
                            </button>
                            <span className="text-sm text-gray-700">Page {page} of {data.pages}</span>
                            <button
                                onClick={() => updateParam('page', String(page + 1))}
                                disabled={page >= data.pages}
                                className="btn-secondary py-1.5 px-2.5 disabled:opacity-40"
                            >
                                <ChevronRight className="w-4 h-4" />
                            </button>
                        </div>
                    </div>
                )}
            </div>

            {/* Delete confirmation modal */}
            {confirmDelete && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50">
                    <div className="card p-6 w-full max-w-md">
                        <h3 className="font-semibold text-gray-900 text-lg">Delete Product</h3>
                        <p className="text-gray-600 mt-2">
                            Are you sure you want to delete <strong>{confirmDelete.name}</strong>? This action cannot be undone.
                        </p>
                        <div className="flex gap-3 mt-6 justify-end">
                            <button onClick={() => setConfirmDelete(null)} className="btn-secondary">
                                Cancel
                            </button>
                            <button onClick={() => handleDelete(confirmDelete)} className="btn-danger">
                                <Trash2 className="w-4 h-4" />
                                Delete
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}
