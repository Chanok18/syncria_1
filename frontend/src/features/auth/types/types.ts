interface LoginRequest {
  email: string
  password: string
}

interface RegisterRequest {
  email: string
  password: string
  fullName: string
}

interface AuthResponse {
  token: string
  email: string
  fullName: string
  role: string
}

export type { LoginRequest, RegisterRequest, AuthResponse }
