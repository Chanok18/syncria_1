import axios from 'axios'

const baseURL = import.meta.env.VITE_API_URL || '/api/v1'

const api = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && !window.location.pathname.startsWith('/login') && !window.location.pathname.startsWith('/register')) {
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
