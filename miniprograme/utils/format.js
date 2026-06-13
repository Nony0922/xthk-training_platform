const weekdays = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日']
const attendStatus = ['', '正常', '迟到', '早退', '缺勤', '请假']
const orderStatus = ['待支付', '已支付', '已取消']
const examStatus = ['未开始', '进行中', '已结束']

module.exports = {
  weekday: (n) => weekdays[n] || '-',
  attend: (n) => attendStatus[n] || '-',
  order: (n) => orderStatus[n] || '-',
  exam: (n) => examStatus[n] || '-',
  msg: (n) => (n === 1 ? '已回复' : '待回复'),
  leaveType: (n) => ['', '事假', '病假', '其他'][n] || '-',
  leaveStatus: (n) => ['待审批', '已通过', '已驳回', '已撤回'][n] || '-',
  attendClass: (n) => ['', 'tag-green', 'tag-orange', 'tag-orange', 'tag-red', 'tag-purple'][n] || 'tag-gray',
  examClass: (n) => ['tag-orange', 'tag-green', 'tag-gray'][n] || 'tag-gray'
}
