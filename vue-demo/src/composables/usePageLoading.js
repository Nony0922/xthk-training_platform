import { ref } from 'vue'

export function usePageLoading(initial = false) {
  const pageLoading = ref(initial)

  const withLoading = async (fn) => {
    pageLoading.value = true
    try {
      await fn()
    } finally {
      pageLoading.value = false
    }
  }

  return { pageLoading, withLoading }
}
