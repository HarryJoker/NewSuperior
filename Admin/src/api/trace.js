import request from '@/utils/request'

export function fetchTraces(taskId, unitId) {
  return request({
    url: '/vue-element-admin/trace/getTraceListByTaskAndUnit/' + taskId + '/' + unitId,
    method: 'get'
  })
}

// 审核进展（快慢正常）
export function newVerifyTrace(data) {
  return request({
    url: '/vue-element-admin/trace/newVerifyTrace',
    method: 'post',
    data
  })
}

// 上报领导
export function newReportTrace(data) {
  return request({
    url: '/vue-element-admin/trace/newReportTrace',
    method: 'post',
    data
  })
}

// 任务催报
export function newRushTrace(data) {
  return request({
    url: '/vue-element-admin/trace/newRushTrace',
    method: 'post',
    data
  })
}

// 退出重报
export function newBackTrace(data) {
  return request({
    url: '/vue-element-admin/trace/newBackTrace',
    method: 'post',
    data
  })
}
