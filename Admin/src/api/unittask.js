import request from '@/utils/request'

export function deleteUnitTask(taskId, unitId) {
  return request({
    url: '/vue-element-admin/unitTask/delete/' + taskId + '/' + unitId,
    method: 'get'
  })
}
