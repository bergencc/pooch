/** @type {import('tailwindcss').Config} */
export default {
    content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
    theme: {
        extend: {
            colors: {
                brand: {
                    50: '#fef9ee',
                    100: '#fdf0d5',
                    200: '#fbddaa',
                    300: '#f8c374',
                    400: '#f4a03c',
                    500: '#f18418',
                    600: '#e26a0e',
                    700: '#bb500d',
                    800: '#953f12',
                    900: '#793512',
                    950: '#411806',
                },
            },
        },
    },
    plugins: [],
}