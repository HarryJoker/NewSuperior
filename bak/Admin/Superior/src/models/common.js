import { queryList, removeEntry, addEntry, updateEntry, queryToken } from '@/services/api';

export default {
  namespace: 'common',

  state: {
    data: {
      token: {},
      list: [],
      pagination: {},
    },
  },

  effects: {
    *fetch({ payload, callback }, { call, put }) {

      console.log(payload);

      const response = yield call(queryList, payload);
      if (response.errorCode == 0) {
         yield put({
            type: 'save',
            payload: response.content,
          });
      } 

      if (callback) callback(response.errorCode == 0 ? '获取成功' : '获取失败');
      
    },
    *fetchToken({ payload, callback }, { call, put }) {
      const response = yield call(queryToken, payload);
      if (response.errorCode == 0) {
         yield put({
            type: 'saveToken',
            payload: response.content.token,
          });
      }

      if (callback) callback(response.errorCode == 0 ? '获取成功' : '获取失败')
      
    },

    *add({ payload, callback }, { call, put }) {
      const response = yield call(addEntry, payload);
      if (response.errorCode == 0) {
        yield put({
          type: 'append',
          payload: response.content,
        });
      }
      
      if (callback) callback(response.errorCode == 0 ? '添加成功' : '添加失败');
    },
    *remove({ payload, callback }, { call, put }) {
      const response = yield call(removeEntry, payload);
      if (response.errorCode == 0) {
        yield put({
          type: 'delete',
          payload: payload,
        });
      }
      if (callback) callback(response.errorCode == 0 ? '删除成功' : '删除失败');
    },
    *update({ payload, callback }, { call, put }) {
      const {reducer, ...params} = payload;
      const response = yield call(updateEntry, params);
      if (response.errorCode == 0) {
        yield put({
          type: reducer ? 'delete' : 'replace',
          payload: reducer ? {ids:[params.id]} : response.content,
        });
      }
      if (callback) callback(response.errorCode == 0 ? '更新成功' : '更新失败');
    },
  },

  reducers: {
    save(state, action) {
      return {...state, data: action.payload};
    },
    saveToken(state, action) {
      return {...state, data: {...state.data, token: action.payload}};
    },
    replace(state, action) {
      let list = state.data.list.map(item => {return item.id == action.payload.id ? action.payload : item});
      return {...state, data: {...state.data, list: list}};
    },
    append(state, action) {
      let list = [...state.data.list, action.payload];
      return {...state, data: {...state.data, list: list}};
    },
    delete(state, action) {
      let list = state.data.list.filter(item => {return !action.payload.ids.includes(item.id)});
      return {...state, data: {...state.data, list: list}};
    },
  },
};
