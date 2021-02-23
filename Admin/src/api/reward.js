import request from '@/utils/request'

export function fetchList() {
  return request({
    url: '/vue-element-admin/reward/getRewards',
    method: 'get'
  })
}

export function newReview(data) {
  return request({
    url: '/vue-element-admin/review/newReward',
    method: 'post',
    data
  })
}

export function updateReview(data) {
  return request({
    url: '/vue-element-admin/review/updateReward/' + data.id,
    method: 'post',
    data
  })
}

export function deleteReview(id) {
  return request({
    url: '/vue-element-admin/review/deletReview/' + id,
    method: 'get'
  })
}
