import React, { useEffect, useState, useRef } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import api from '@/lib/api'
import type {Product} from '@/types'
import {
    ArrowLeft, Save, Plus, X, Upload, Image, AlertCircle, CheckCircle2
} from 'lucide-react'
import { getErrorMessage, ECO_SCORE_COLORS, cn } from '@/lib/utils'

const productSchema = z.object({
    barcode: z.string().min(1, 'Barcode is required').max(50),
    name: z.string().min(1, 'Product name is required').max(255),
    brand: z.string().max(255).optional(),
    product_type: z.enum(['food', 'treat', 'medication', 'supplement', 'toy', 'other', '']).optional(),
    ingredients: z.array(z.string()).default([]),
    eco_score: z.enum(['A', 'B', 'C', 'D', 'E', 'F', '']).optional(),
    nutrition_info: z.record(z.string()).optional(),
})

type ProductForm = z.infer<typeof productSchema>

const ECO_SCORES = ['A', 'B', 'C', 'D', 'E', 'F'] as const
const PRODUCT_TYPES = ['food', 'treat', 'medication', 'supplement', 'toy', 'other'] as const

export default function ProductFormPage() {
    const { id } = useParams()
    const navigate = useNavigate()
    const isEdit = Boolean(id)
    const fileInputRef = useRef<HTMLInputElement>(null)

    const [loading, setLoading] = useState(isEdit)
    const [saving, setSaving] = useState(false)
    const [uploadingPhoto, setUploadingPhoto] = useState(false)
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')
    const [product, setProduct] = useState<Product | null>(null)
    const [ingredientInput, setIngredientInput] = useState('')
    const [nutritionKey, setNutritionKey] = useState('')
    const [nutritionValue, setNutritionValue] = useState('')

    const {
        register,
        handleSubmit,
        control,
        reset,
        watch,
        setValue,
        getValues,
        formState: { errors },
    } = useForm<ProductForm>({
        resolver: zodResolver(productSchema),
        defaultValues: { ingredients: [] },
    })

    const ingredients = watch('ingredients') ?? []

    useEffect(() => {
        if (!isEdit) return

        api.get(`/admin/products/${id}`)
            .then((res) => {
                const p: Product = res.data

                setProduct(p)
                reset({
                    barcode: p.barcode,
                    name: p.name,
                    brand: p.brand ?? '',
                    product_type: (p.product_type as ProductForm['product_type']) ?? '',
                    ingredients: p.ingredients ?? [],
                    eco_score: (p.eco_score as ProductForm['eco_score']) ?? '',
                    nutrition_info: p.nutrition_info ?? {},
                })
            })
            .catch(() => setError('Product not found'))
            .finally(() => setLoading(false))
    }, [id, isEdit, reset])

    const onSubmit = async (data: ProductForm) => {
        setSaving(true)
        setError('')
        setSuccess('')

        try {
            const payload = {
                ...data,
                product_type: data.product_type || undefined,
                eco_score: data.eco_score || undefined,
                brand: data.brand || undefined,
            }

            if (isEdit) {
                await api.put(`/admin/products/${id}`, payload)

                setSuccess('Product updated successfully!')
            } else {
                const res = await api.post('/admin/products', payload)

                setSuccess('Product created successfully!')
                setTimeout(() => navigate(`/products/${res.data.id}/edit`), 1200)
            }
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setSaving(false)
        }
    }

    const addIngredient = () => {
        const val = ingredientInput.trim().toLowerCase()

        if (val && !ingredients.includes(val)) {
            setValue('ingredients', [...ingredients, val])
        }

        setIngredientInput('')
    }

    const removeIngredient = (i: number) => {
        setValue('ingredients', ingredients.filter((_, idx) => idx !== i))
    }

    const handlePhotoUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
        const file = e.target.files?.[0]

        if (!file || !id) return

        setUploadingPhoto(true)

        try {
            const form = new FormData()

            form.append('file', file)

            const res = await api.post(`/admin/products/${id}/photo`, form, {
                headers: { 'Content-Type': 'multipart/form-data' },
            })

            setProduct(res.data)
            setSuccess('Photo uploaded!')
        } catch (err) {
            setError(getErrorMessage(err))
        } finally {
            setUploadingPhoto(false)
        }
    }

    const addNutrition = () => {
        if (nutritionKey.trim() && nutritionValue.trim()) {
            const current = getValues('nutrition_info') ?? {}

            setValue('nutrition_info', { ...current, [nutritionKey.trim()]: nutritionValue.trim() })
            setNutritionKey('')
            setNutritionValue('')
        }
    }

    const removeNutrition = (key: string) => {
        const current = { ...(getValues('nutrition_info') ?? {}) }

        delete current[key]

        setValue('nutrition_info', current)
    }

    if (loading) {
        return (
            <div className="flex items-center justify-center h-64">
                <div className="w-8 h-8 border-4 border-brand-500 border-t-transparent rounded-full animate-spin" />
            </div>
        )
    }

    return (
        <div className="max-w-3xl mx-auto space-y-6">
            {/* Header */}
            <div className="flex items-center gap-4">
                <button onClick={() => navigate('/products')} className="btn-secondary py-2 px-3">
                    <ArrowLeft className="w-4 h-4" />
                </button>
                <div>
                    <h1 className="text-2xl font-bold text-gray-900">
                        {isEdit ? 'Edit Product' : 'Add New Product'}
                    </h1>
                    {isEdit && product && (
                        <p className="text-gray-500 text-sm mt-0.5">{product.name}</p>
                    )}
                </div>
            </div>

            {/* Alerts */}
            {error && (
                <div className="flex items-start gap-3 p-4 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">
                    <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
                    {error}
                </div>
            )}
            {success && (
                <div className="flex items-start gap-3 p-4 bg-green-50 border border-green-200 rounded-xl text-green-700 text-sm">
                    <CheckCircle2 className="w-5 h-5 flex-shrink-0 mt-0.5" />
                    {success}
                </div>
            )}

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
                {/* Basic info */}
                <div className="card p-6 space-y-5">
                    <h2 className="font-semibold text-gray-900">Basic Information</h2>

                    <div className="grid sm:grid-cols-2 gap-4">
                        <div>
                            <label className="label">Barcode (UPC/EAN) *</label>
                            <input {...register('barcode')} className="input" placeholder="e.g. 012345678901" />
                            {errors.barcode && <p className="mt-1 text-xs text-red-600">{errors.barcode.message}</p>}
                        </div>
                        <div>
                            <label className="label">Product Type</label>
                            <select {...register('product_type')} className="input">
                                <option value="">Select type…</option>
                                {PRODUCT_TYPES.map((t) => (
                                    <option key={t} value={t} className="capitalize">{t.charAt(0).toUpperCase() + t.slice(1)}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div>
                        <label className="label">Product Name *</label>
                        <input {...register('name')} className="input" placeholder="e.g. Hill's Science Diet Adult Chicken" />
                        {errors.name && <p className="mt-1 text-xs text-red-600">{errors.name.message}</p>}
                    </div>

                    <div className="grid sm:grid-cols-2 gap-4">
                        <div>
                            <label className="label">Brand</label>
                            <input {...register('brand')} className="input" placeholder="e.g. Hill's" />
                        </div>
                        <div>
                            <label className="label">Eco Score</label>
                            <div className="flex gap-2">
                                {ECO_SCORES.map((score) => (
                                    <Controller
                                        key={score}
                                        control={control}
                                        name="eco_score"
                                        render={({ field }) => (
                                            <button
                                                type="button"
                                                onClick={() => field.onChange(field.value === score ? '' : score)}
                                                className={cn(
                                                    'flex-1 py-2 rounded-lg text-sm font-bold border-2 transition-all',
                                                    field.value === score
                                                        ? `${ECO_SCORE_COLORS[score]} border-current`
                                                        : 'border-gray-200 text-gray-400 hover:border-gray-300'
                                                )}
                                            >
                                                {score}
                                            </button>
                                        )}
                                    />
                                ))}
                            </div>
                            <p className="text-xs text-gray-400 mt-1">A = best, F = worst sustainability</p>
                        </div>
                    </div>
                </div>

                {/* Ingredients */}
                <div className="card p-6 space-y-4">
                    <h2 className="font-semibold text-gray-900">Ingredients</h2>
                    <p className="text-sm text-gray-500">
                        Add ingredients one by one. The recommendation engine uses these to detect allergens and harmful substances.
                    </p>

                    <div className="flex gap-2">
                        <input
                            value={ingredientInput}
                            onChange={(e) => setIngredientInput(e.target.value)}
                            onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addIngredient() } }}
                            placeholder="e.g. chicken, xylitol, salmon…"
                            className="input flex-1"
                        />
                        <button type="button" onClick={addIngredient} className="btn-secondary">
                            <Plus className="w-4 h-4" />
                            Add
                        </button>
                    </div>

                    {ingredients.length > 0 ? (
                        <div className="flex flex-wrap gap-2">
                            {ingredients.map((ing, i) => (
                                <span
                                    key={i}
                                    className="inline-flex items-center gap-1.5 px-3 py-1 bg-gray-100 text-gray-700 rounded-full text-sm"
                                >
                  {ing}
                                    <button
                                        type="button"
                                        onClick={() => removeIngredient(i)}
                                        className="text-gray-400 hover:text-red-500 transition-colors"
                                    >
                    <X className="w-3 h-3" />
                  </button>
                </span>
                            ))}
                        </div>
                    ) : (
                        <p className="text-sm text-gray-400 italic">No ingredients added yet</p>
                    )}
                </div>

                {/* Nutrition info */}
                <div className="card p-6 space-y-4">
                    <h2 className="font-semibold text-gray-900">Nutrition Facts (Optional)</h2>
                    <div className="flex gap-2">
                        <input
                            value={nutritionKey}
                            onChange={(e) => setNutritionKey(e.target.value)}
                            placeholder="Nutrient (e.g. Protein)"
                            className="input flex-1"
                        />
                        <input
                            value={nutritionValue}
                            onChange={(e) => setNutritionValue(e.target.value)}
                            placeholder="Value (e.g. 26%)"
                            className="input w-32"
                        />
                        <button type="button" onClick={addNutrition} className="btn-secondary">
                            <Plus className="w-4 h-4" />
                        </button>
                    </div>

                    <Controller
                        control={control}
                        name="nutrition_info"
                        render={({ field }) => (
                            <>
                                {Object.keys(field.value ?? {}).length > 0 ? (
                                    <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                                        {Object.entries(field.value ?? {}).map(([k, v]) => (
                                            <div key={k} className="flex items-center justify-between p-2 bg-gray-50 rounded-lg text-sm">
                                                <span className="text-gray-600">{k}: <span className="font-medium text-gray-900">{v}</span></span>
                                                <button type="button" onClick={() => removeNutrition(k)} className="text-gray-400 hover:text-red-500 ml-2">
                                                    <X className="w-3 h-3" />
                                                </button>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <p className="text-sm text-gray-400 italic">No nutrition data added</p>
                                )}
                            </>
                        )}
                    />
                </div>

                {/* Photo upload (only when editing) */}
                {isEdit && (
                    <div className="card p-6 space-y-4">
                        <h2 className="font-semibold text-gray-900">Product Photo</h2>
                        <div className="flex items-center gap-4">
                            {product?.photo_url ? (
                                <img
                                    src={product.photo_url}
                                    alt="Product"
                                    className="w-20 h-20 rounded-xl object-cover border border-gray-200"
                                />
                            ) : (
                                <div className="w-20 h-20 bg-gray-100 rounded-xl flex items-center justify-center border border-dashed border-gray-300">
                                    <Image className="w-8 h-8 text-gray-300" />
                                </div>
                            )}
                            <div>
                                <button
                                    type="button"
                                    onClick={() => fileInputRef.current?.click()}
                                    disabled={uploadingPhoto}
                                    className="btn-secondary"
                                >
                                    {uploadingPhoto ? (
                                        <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin" />
                                    ) : (
                                        <Upload className="w-4 h-4" />
                                    )}
                                    {product?.photo_url ? 'Replace Photo' : 'Upload Photo'}
                                </button>
                                <p className="text-xs text-gray-400 mt-1">JPG, PNG or WebP, max 5MB</p>
                            </div>
                        </div>
                        <input
                            ref={fileInputRef}
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={handlePhotoUpload}
                        />
                    </div>
                )}

                {/* Actions */}
                <div className="flex items-center justify-between">
                    <button type="button" onClick={() => navigate('/products')} className="btn-secondary">
                        Cancel
                    </button>
                    <button type="submit" disabled={saving} className="btn-primary">
                        {saving ? (
                            <span className="flex items-center gap-2">
                <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                Saving…
              </span>
                        ) : (
                            <>
                                <Save className="w-4 h-4" />
                                {isEdit ? 'Save Changes' : 'Create Product'}
                            </>
                        )}
                    </button>
                </div>
            </form>
        </div>
    )
}
