import request from '@/utils/request'

export function fetchList(category) {
  return request({
    url: '/vue-element-admin/draft/getDrafts/' + category,
    method: 'get'
  })
}

export function createDraft(data) {
  console.log(data)
  return request({
    url: '/vue-element-admin/draft/newDraft',
    method: 'post',
    data
  })
}

export function updateDraft(data) {
  return request({
    url: '/vue-element-admin/draft/updateDraft/' + data.id,
    method: 'post',
    data
  })
}

export function deleteDraft(id) {
  return request({
    url: '/vue-element-admin/draft/deleteDrarft/' + id,
    method: 'get'
  })
}
