import request from '@/utils/request'

export function fetchList(query) {
  return request({
    url: '/vue-element-admin/opinion/getOpinions',
    method: 'get',
    params: query
  })
}

export function deletOpinion(opinionId) {
  return request({
    url: '/vue-element-admin/opinion/deletOpinion/' + opinionId,
    method: 'get'
  })
}

export function updateOpinion(data) {
  return request({
    url: '/vue-element-admin/opinion/updateOpinion/' + data.id,
    method: 'post',
    data
  })
}
