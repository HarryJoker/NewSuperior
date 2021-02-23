import request from '@/utils/request'

export function fetchList() {
  return request({
    url: '/vue-element-admin/clue/getClues',
    method: 'get'
  })
}

// export function deletOpinion(opinionId) {
//   return request({
//     url: '/vue-element-admin/opinion/deletOpinion/' + opinionId,
//     method: 'get'
//   })
// }

export function updateClue(data) {
  return request({
    url: '/vue-element-admin/clue/updateClue/' + data.id,
    method: 'post',
    data
  })
}
