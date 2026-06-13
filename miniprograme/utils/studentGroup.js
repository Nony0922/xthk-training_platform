function trimTime(t) {
  if (!t) return '-'
  return String(t).substring(0, 5)
}

function groupByStudentClass(students, classItems, mapRow, sortFn) {
  return (students || []).map(student => {
    let rows = (classItems || [])
      .filter(i => i.classId === student.classId)
      .map(mapRow)
    if (sortFn) rows = rows.sort(sortFn)
    return {
      studentId: student.id,
      studentName: student.name,
      className: student.className || '未分班',
      rows
    }
  })
}

function groupByStudentRecords(students, records, mapRow, sortFn) {
  return (students || []).map(student => {
    let rows = (records || [])
      .filter(i => i.studentId === student.id)
      .map(mapRow)
    if (sortFn) rows = rows.sort(sortFn)
    return {
      studentId: student.id,
      studentName: student.name,
      className: student.className || '未分班',
      rows
    }
  })
}

function hasAnyRows(groups) {
  return (groups || []).some(g => g.rows && g.rows.length > 0)
}

module.exports = {
  trimTime,
  groupByStudentClass,
  groupByStudentRecords,
  hasAnyRows
}
