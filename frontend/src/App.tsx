import { ChangeEvent, CSSProperties, FormEvent, useState } from 'react'

type FormState = {
  title: string
  description: string
  instructor: string
  durationMinutes: string
  price: string
}

type SubmitState =
  | { status: 'idle' }
  | { status: 'submitting' }
  | { status: 'success'; id: number | string }
  | { status: 'error'; message: string }

const initialForm: FormState = {
  title: '',
  description: '',
  instructor: '',
  durationMinutes: '',
  price: '',
}

function App() {
  const [form, setForm] = useState<FormState>(initialForm)
  const [submit, setSubmit] = useState<SubmitState>({ status: 'idle' })

  const update =
    (field: keyof FormState) =>
    (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
      setForm((prev) => ({ ...prev, [field]: e.target.value }))
    }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setSubmit({ status: 'submitting' })

    try {
      const response = await fetch('/api/master-classes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          title: form.title.trim(),
          description: form.description.trim(),
          instructor: form.instructor.trim(),
          durationMinutes: Number(form.durationMinutes),
          price: Number(form.price),
        }),
      })

      if (!response.ok) {
        const text = await response.text()
        setSubmit({
          status: 'error',
          message: text || `Ошибка сервера (${response.status})`,
        })
        return
      }

      const created = (await response.json()) as { id: number | string }
      setSubmit({ status: 'success', id: created.id })
      setForm(initialForm)
    } catch (err) {
      setSubmit({
        status: 'error',
        message:
          err instanceof Error
            ? err.message
            : 'Неизвестная ошибка отправки запроса',
      })
    }
  }

  const isSubmitting = submit.status === 'submitting'

  return (
    <main style={styles.main}>
      <h1 style={styles.heading}>Новый мастер-класс</h1>

      <form onSubmit={handleSubmit} style={styles.form} noValidate>
        <label style={styles.label}>
          <span>Название</span>
          <input
            value={form.title}
            onChange={update('title')}
            required
            disabled={isSubmitting}
            style={styles.input}
          />
        </label>

        <label style={styles.label}>
          <span>Описание</span>
          <textarea
            value={form.description}
            onChange={update('description')}
            rows={3}
            disabled={isSubmitting}
            style={styles.input}
          />
        </label>

        <label style={styles.label}>
          <span>Инструктор</span>
          <input
            value={form.instructor}
            onChange={update('instructor')}
            required
            disabled={isSubmitting}
            style={styles.input}
          />
        </label>

        <label style={styles.label}>
          <span>Длительность, мин</span>
          <input
            type="number"
            min={1}
            value={form.durationMinutes}
            onChange={update('durationMinutes')}
            required
            disabled={isSubmitting}
            style={styles.input}
          />
        </label>

        <label style={styles.label}>
          <span>Цена, ₽</span>
          <input
            type="number"
            min={0}
            step="0.01"
            value={form.price}
            onChange={update('price')}
            required
            disabled={isSubmitting}
            style={styles.input}
          />
        </label>

        <button type="submit" disabled={isSubmitting} style={styles.button}>
          {isSubmitting ? 'Сохраняем…' : 'Создать'}
        </button>
      </form>

      {submit.status === 'success' && (
        <p style={styles.success}>
          Мастер-класс создан (id: {String(submit.id)})
        </p>
      )}

      {submit.status === 'error' && (
        <p style={styles.error}>Не удалось создать: {submit.message}</p>
      )}
    </main>
  )
}

const styles: Record<string, CSSProperties> = {
  main: {
    maxWidth: 480,
    margin: '40px auto',
    padding: '0 16px',
    fontFamily: 'system-ui, -apple-system, sans-serif',
    color: '#1a1a1a',
  },
  heading: { marginBottom: 24, fontSize: 24 },
  form: { display: 'flex', flexDirection: 'column', gap: 16 },
  label: {
    display: 'flex',
    flexDirection: 'column',
    gap: 6,
    fontSize: 14,
    color: '#555',
  },
  input: {
    fontSize: 16,
    padding: '10px 12px',
    border: '1px solid #ccc',
    borderRadius: 6,
    fontFamily: 'inherit',
    color: '#1a1a1a',
  },
  button: {
    fontSize: 16,
    padding: '12px 16px',
    backgroundColor: '#1a73e8',
    color: '#fff',
    border: 'none',
    borderRadius: 6,
    cursor: 'pointer',
    marginTop: 8,
  },
  success: { marginTop: 16, color: '#0a8043' },
  error: { marginTop: 16, color: '#c5221f' },
}

export default App
