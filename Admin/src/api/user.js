import request from '@/utils/request'

export function fetchList(role) {
  return request({
    url: '/vue-element-admin/user/getUsers/' + role,
    method: 'get'
  })
}

export function deleteUser(userId) {
  return request({
    url: '/vue-element-admin/user/delete/' + userId,
    method: 'get'
  })
}

export function login(data) {
  return request({
    url: '/vue-element-admin/admin/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/vue-element-admin/admin/info/' + token,
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/vue-element-admin/admin/logout',
    method: 'post'
  })
}
