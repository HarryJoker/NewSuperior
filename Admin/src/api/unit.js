import request from '@/utils/request'

export function fetchUnits() {
  return request({
    url: '/vue-element-admin/unit/getAllUnitWithUser',
    method: 'get'
  })
}

export function fetchCommonUnits() {
  return request({
    url: '/vue-element-admin/unit/getUnitsByRole/4',
    method: 'get'
  })
}

export function getMapCommonUnits() {
  return request({
    url: '/vue-element-admin/unit/getMapCommonUnits',
    method: 'get'
  })
}

export function fetchLeaderUnits() {
  return request({
    url: '/vue-element-admin/unit/getLeaderUnits',
    method: 'get'
  })
}

export function createUnit(data) {
  return request({
    url: '/vue-element-admin/unit/createUnit',
    method: 'post',
    data
  })
}

export function updateUnit(data) {
  return request({
    url: '/vue-element-admin/unit/updateUnit/' + data.id,
    method: 'post',
    data
  })
}

export function deleteUnit(unitId) {
  return request({
    url: '/vue-element-admin/unit/delete/' + unitId,
    method: 'get'
  })
}
