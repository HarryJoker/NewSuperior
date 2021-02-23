import request from '@/utils/request'

export function fetchList() {
  return request({
    url: '/vue-element-admin/version/getVersions',
    method: 'get'
  })
}

export function createVersion(data) {
  console.log(data)
  return request({
    url: '/vue-element-admin/version/newVersion',
    method: 'post',
    data
  })
}
