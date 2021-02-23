import request from '@/utils/request'

export function fetchList(draftId) {
  return request({
    url: '/vue-element-admin/draftTask/getDraftTasks/' + draftId,
    method: 'get'
  })
}

export function createDraftTasks(data) {
  return request({
    url: '/vue-element-admin/draftTask/createDraftTasks',
    method: 'post',
    data
  })
}

export function deployDraftTask(draftTaskId) {
  return request({
    url: '/vue-element-admin/draftTask/deployDraftTask/' + draftTaskId,
    method: 'get'
  })
}

export function deployDraftTasks(data) {
  return request({
    url: '/vue-element-admin/draftTask/deployDraftTasks',
    method: 'post',
    data
  })
}

export function updateDraftTask(data) {
  return request({
    url: '/vue-element-admin/draftTask/updateDraftTask/' + data.id,
    method: 'post',
    data
  })
}

export function deleteDraftTask(id) {
  return request({
    url: '/vue-element-admin/draftTask/deleteDraftTask/' + id,
    method: 'get'
  })
}

export function deleteDraftTasks(data) {
  return request({
    url: '/vue-element-admin/draftTask/deleteDraftTasks',
    method: 'post',
    data
  })
}
