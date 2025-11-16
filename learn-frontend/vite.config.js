import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  base: '/Learning-new-thing/',
  server: {
    proxy: {
      '/api': {
        target: 'https://learning-new-thing.onrender.com',
        changeOrigin: true,
      },
    },
  },
})
