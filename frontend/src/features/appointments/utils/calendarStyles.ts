export const appointmentColors: Record<string, { bg: string; border: string; text: string }> = {
  SCHEDULED: { bg: '#dbeafe', border: '#3b82f6', text: '#1e40af' },
  COMPLETED: { bg: '#dcfce7', border: '#22c55e', text: '#166534' },
  CANCELLED: { bg: '#fee2e2', border: '#ef4444', text: '#991b1b' },
}

export function getEventStyle(event: { status: string }) {
  const defaultColor = appointmentColors.SCHEDULED
  const colors = appointmentColors[event.status] ?? defaultColor
  if (!colors) return { style: {} }
  return {
    style: {
      backgroundColor: colors.bg,
      borderLeft: `4px solid ${colors.border}`,
      color: colors.text,
      borderRadius: '4px',
      fontSize: '12px',
      padding: '2px 6px',
    },
  }
}
