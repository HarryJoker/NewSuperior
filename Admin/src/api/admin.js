import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/vue-element-admin/admin/login',
    method: 'post',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/vue-element-admin/admin/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/vue-element-admin/admin/logout',
    method: 'post'
  })
}
