import request from '@/utils/request'

export function fetchList() {
  return request({
    url: '/vue-element-admin/comment/getComments',
    method: 'get'
  })
}

export function updateComment(data) {
  return request({
    url: '/vue-element-admin/comment/updateComment/' + data.id,
    method: 'post',
    data
  })
}
