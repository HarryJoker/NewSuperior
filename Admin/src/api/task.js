import request from '@/utils/request'

export function fetchTaskList(category) {
  return request({
    url: '/vue-element-admin/task/getTaskListByCategory/' + category,
    method: 'get'
  })
}

export function fetchPeopleProgressTaskList() {
  return request({
    url: '/vue-element-admin/task/getPeopleProgressTaskList',
    method: 'get'
  })
}

export function fetchGovermentPublicTaskList() {
  return request({
    url: '/vue-element-admin/task/getGovermentPublicTaskList',
    method: 'get'
  })
}

export function fetchTaskListByFilter(data) {
  return request({
    url: '/vue-element-admin/task/getTaskListByFilter/' + data.category,
    method: 'post',
    data
  })
}

export function fetchMonthContentTraceList(category) {
  return request({
    url: '/vue-element-admin/trace/getMonthUnitTaskTrace/' + category,
    method: 'get'
  })
}

export function createTask(data) {
  console.log(data)
  return request({
    url: '/vue-element-admin/task/newTask',
    method: 'post',
    data
  })
}

export function updateTask(data) {
  return request({
    url: '/vue-element-admin/task/update/' + data.id,
    method: 'post',
    data
  })
}

export function deleteTask(id) {
  return request({
    url: '/vue-element-admin/task/delete/' + id,
    method: 'get'
  })
}

export function deleteTasks(data) {
  return request({
    url: '/vue-element-admin/task/deleteTasks',
    method: 'post',
    data
  })
}
