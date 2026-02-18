import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}

export function formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
    })
}

export function formatDateTime(dateStr: string): string {
    return new Date(dateStr).toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
    })
}

export function getErrorMessage(error: unknown): string {
    if (error && typeof error === 'object' && 'response' in error) {
        const res = (error as { response: { data: { detail: unknown } } }).response
        const detail = res?.data?.detail

        if (typeof detail === 'string') return detail

        if (Array.isArray(detail)) return detail.map((e) => e.msg).join(', ')
    }

    if (error instanceof Error) return error.message

    return 'An unexpected error occurred'
}

export const ECO_SCORE_COLORS: Record<string, string> = {
    A: 'bg-green-100 text-green-800',
    B: 'bg-emerald-100 text-emerald-800',
    C: 'bg-yellow-100 text-yellow-800',
    D: 'bg-orange-100 text-orange-800',
    E: 'bg-red-100 text-red-800',
    F: 'bg-red-200 text-red-900',
}

export const PRODUCT_TYPE_LABELS: Record<string, string> = {
    food: 'Food',
    treat: 'Treat',
    medication: 'Medication',
    supplement: 'Supplement',
    toy: 'Toy',
    other: 'Other',
}
