import { computed } from 'vue'
import { useRoute } from 'vue-router'

export function useReadOnly() {
  const route = useRoute()
  return computed(() => route.meta.readOnly === true)
}
