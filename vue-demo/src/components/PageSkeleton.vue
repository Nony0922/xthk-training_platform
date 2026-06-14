<template>
  <div class="page-skeleton" :class="`page-skeleton--${variant}`">
    <div v-if="showToolbar" class="sk-toolbar">
      <div class="sk-block sk-btn" />
    </div>

    <template v-if="variant === 'table'">
      <div class="sk-table">
        <div class="sk-row sk-row--head">
          <div v-for="i in cols" :key="i" class="sk-cell" />
        </div>
        <div v-for="r in rows" :key="r" class="sk-row">
          <div v-for="c in cols" :key="c" class="sk-cell" />
        </div>
      </div>
    </template>

    <template v-else-if="variant === 'cards'">
      <div v-for="g in groups" :key="g" class="sk-card">
        <div class="sk-card-head">
          <div class="sk-block sk-title" />
          <div class="sk-block sk-badge" />
        </div>
        <div class="sk-block sk-line sk-line--long" />
        <div class="sk-block sk-line sk-line--medium" />
        <div class="sk-block sk-line sk-line--short" />
      </div>
    </template>

    <template v-else>
      <div v-for="g in groups" :key="g" class="sk-group">
        <div class="sk-group-head">
          <div class="sk-block sk-title" />
          <div class="sk-block sk-badge" />
        </div>
        <div class="sk-subgroup">
          <div class="sk-subgroup-head">
            <div class="sk-block sk-subtitle" />
            <div class="sk-block sk-badge sk-badge--sm" />
          </div>
          <div class="sk-table sk-table--compact">
            <div class="sk-row sk-row--head">
              <div v-for="i in cols" :key="i" class="sk-cell" />
            </div>
            <div v-for="r in rows" :key="r" class="sk-row">
              <div v-for="c in cols" :key="c" class="sk-cell" />
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
defineProps({
  variant: {
    type: String,
    default: 'grouped',
    validator: (v) => ['table', 'grouped', 'cards'].includes(v)
  },
  groups: { type: Number, default: 2 },
  rows: { type: Number, default: 4 },
  cols: { type: Number, default: 6 },
  showToolbar: { type: Boolean, default: true }
})
</script>

<style scoped>
@import '@/assets/manage.css';
</style>
