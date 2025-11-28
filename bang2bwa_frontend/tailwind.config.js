/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#AE8B72',
        secondary: '#CEAC93',
        accent: '#E3C9A5',
        bg: '#FFFBE8',
        dark: '#333333',
      },
      fontFamily: {
        sans: ['Noto Sans KR', 'sans-serif'],
      }
    },
  },
  plugins: [],
}