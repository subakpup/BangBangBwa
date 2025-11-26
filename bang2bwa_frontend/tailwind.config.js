/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: "#AE8B72", // 메인 갈색
        secondary: "#CEAC93", // 서브 갈색
        accent: "#E3C9A5", // 포인트 베이지
        bg: "#FFFBE8", // 배경 크림색
        dark: "#333333",
      },
      fontFamily: {
        sans: ["Noto Sans KR", "sans-serif"],
      },
    },
  },
  plugins: [],
};
