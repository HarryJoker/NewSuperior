import React, { PureComponent, Fragment } from 'react';
import { connect } from 'dva';
import {
  Card,
  Button,
  Modal,
  message,
} from 'antd';
import StandardTable from '@/components/StandardTable';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './Index.less';
import router from 'umi/router';


const action = 'article';

/* eslint react/no-multi-comp:0 */
@connect(({ common, loading }) => ({
  common,
  loading: loading.models.common,
}))

class TableList extends PureComponent {
  state = {
    expandForm: false,
    selectedRows: [],
  };

  columns = [
    {
      title: '展示图片',
      dataIndex: 'cover',
      width:'15%',
      render: (text, record) => (<img style={{ width: '200px', height: '120px' }} src={record.cover} />)
    },
    {
      title: '文章标题',
      dataIndex: 'title',
      width: '30%',
    },

    {
      title: '创建时间',
      dataIndex: 'createdTime',
    },
    {
      title: '操作',
      render: (text, record) => (
        <Fragment>
          <a onClick={() => this.confirmDelete(record)} >删除</a>
        </Fragment>
      ),
    },
  ];

  componentDidMount() {
    console.log('Component DID MOUNT!')

    const { dispatch } = this.props;
    console.log(this.props);
    dispatch({
      type: 'common/fetch',
      payload: { action },
    });
  }

  componentWillMount() {
    console.log('Component WILL MOUNT!')
  }
  componentWillReceiveProps(newProps) {
        console.log('Component WILL RECEIVE PROPS!')
  }
  shouldComponentUpdate(newProps, newState) {
        return true;
  }
  componentWillUpdate(nextProps, nextState) {
        console.log('Component WILL UPDATE!');
  }
  componentDidUpdate(prevProps, prevState) {
        console.log('Component DID UPDATE!')
  }
  componentWillUnmount() {
        console.log('Component WILL UNMOUNT!')
        this.props.common.data = {}
  }


  handleStandardTableChange = (pagination, filtersArg, sorter) => {
    const { dispatch } = this.props;
    const { formValues } = this.state;

    const filters = Object.keys(filtersArg).reduce((obj, key) => {
      const newObj = { ...obj };
      newObj[key] = getValue(filtersArg[key]);
      return newObj;
    }, {});

    const params = {
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
      ...formValues,
      ...filters,
    };
    if (sorter.field) {
      params.sorter = `${sorter.field}_${sorter.order}`;
    }

    dispatch({
      type: 'common/fetch',
      payload: params,
    });
  };



  handleSelectRows = rows => {
    this.setState({
      selectedRows: rows,
    });
  };

  handleMenuClick = (record) => {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;

    const ids = selectedRows.length > 0 ? selectedRows.map(row => row.id) : [record.id];

    dispatch({
      type: 'banner/remove',
      payload: {
        ids: ids,
      },
      callback: () => {
        this.setState({
          selectedRows: [],
        });
        message.success('删除成功');
      },
    });
  };

  confirmDelete = (currentItem) => {
    Modal.confirm({
      title: '删除任务',
      content: '确定删除该任务吗？',
      okText: '确认',
      cancelText: '取消',
      onOk: () => this.handleMenuClick(currentItem),
    });
  };


  handleCreate = () => {
    console.log('handle create.........................')
    router.push('create')
  }


  render() {
    const {common: { data }, loading,} = this.props;
    const { selectedRows, } = this.state;

    return (
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListOperator}>
              <Button icon="plus" type="primary" onClick={() => this.handleCreate()}>
                新建
              </Button>
              {selectedRows.length > 0 && (
                <span>
                  <Button onClick={() => this.confirmDelete()} selectedKeys={[]}>批量删除</Button>
                </span>
              )}
            </div>
            <StandardTable
              selectedRows={selectedRows}
              rowKey={record => record.id}
              loading={loading}
              data={data}
              columns={this.columns}
              onSelectRow={this.handleSelectRows}
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
      </PageHeaderWrapper>
    );
  }
}

export default TableList;
