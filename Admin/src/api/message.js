import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/vue-element-admin/message/getMessageList',
    method: 'get',
    params: query
  })
}

export function createMessage(data) {
  console.log(data)
  return request({
    url: '/vue-element-admin/message/newMessage',
    method: 'post',
    data
  })
}

export function updateMessage(data) {
  return request({
    url: '/vue-element-admin/message/update/' + data.id,
    method: 'post',
    data
  })
}

export function deleteMessage(id) {
  return request({
    url: '/vue-element-admin/message/delete/' + id,
    method: 'get'
  })
}

export function fetchArticle(id) {
  return request({
    url: '/vue-element-admin/article/detail',
    method: 'get',
    params: { id }
  })
}

export function fetchPv(pv) {
  return request({
    url: '/vue-element-admin/article/pv',
    method: 'get',
    params: { pv }
  })
}

