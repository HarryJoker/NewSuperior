import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/vue-element-admin/banner/getbanners',
    method: 'get',
    params: query
  })
}

export function createBanner(data) {
  console.log(data)
  return request({
    url: '/vue-element-admin/banner/newBanner',
    method: 'post',
    data
  })
}

export function updateBanner(data) {
  return request({
    url: '/vue-element-admin/banner/updateBanner/' + data.id,
    method: 'post',
    data
  })
}

export function deleteBanner(id) {
  return request({
    url: '/vue-element-admin/banner/deleteBanner/' + id,
    method: 'get'
  })
}
