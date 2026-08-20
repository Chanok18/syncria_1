import { Routes, Route } from 'react-router-dom'
import MainLayout from './layouts/MainLayout'
import LoginPage from './features/auth/components/LoginPage'
import RegisterPage from './features/auth/components/RegisterPage'
import ProtectedRoute from './features/auth/components/ProtectedRoute'
import DashboardPage from './features/dashboard/components/DashboardPage'
import ContactListPage from './features/contacts/components/ContactListPage'
import ContactFormPage from './features/contacts/components/ContactFormPage'
import PetListPage from './features/pets/components/PetListPage'
import PetFormPage from './features/pets/components/PetFormPage'
import AppointmentCalendar from './features/appointments/components/AppointmentCalendar'

function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<DashboardPage />} />
        <Route path="contacts" element={<ContactListPage />} />
        <Route path="contacts/new" element={<ContactFormPage />} />
        <Route path="contacts/:id/edit" element={<ContactFormPage />} />
        <Route path="pets" element={<PetListPage />} />
        <Route path="pets/new" element={<PetFormPage />} />
        <Route path="pets/:id/edit" element={<PetFormPage />} />
        <Route path="appointments" element={<AppointmentCalendar />} />
      </Route>
    </Routes>
  )
}

export default App
