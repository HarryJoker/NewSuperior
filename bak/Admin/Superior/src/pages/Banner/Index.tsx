import React, { PureComponent, Fragment } from 'react';
import { connect } from 'dva';
import moment from 'moment';
import {
  Row,
  Col,
  Card,
  Upload,
  Form,
  Input,
  Select,
  Icon,
  Button,
  Dropdown,
  Menu,
  InputNumber,
  DatePicker,
  Modal,
  message,
  Badge,
  Divider,
  Steps,
  Radio,
} from 'antd';
import StandardTable from '@/components/StandardTable';
import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import styles from './Index.less';
import router from 'umi/router';


const FormItem = Form.Item;
const { Step } = Steps;
const { TextArea } = Input;
const { Option } = Select;
const RadioGroup = Radio.Group;


const statusMap = ['default', 'processing', 'success', 'error'];
 // 1老师，2学生，3课程，4自产内容，5，链接网页
const status = ['未定义', '老师', '学生', '课程', '自定义', '网页'];
const descs = ['未定义', '跳转到老师页面', '跳转到学生页面', '跳转到课程页面', '跳转到自定义的内容', '跳转到链接页面'];


const CreateForm = Form.create()(props => {


  const { modalVisible, token, fileList, form, handleAdd, handleChange, getValueFile, handleModalVisible } = props;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      form.resetFields();

      handleAdd(fieldsValue);
    });
  };

  const uploadButton = (
      <div>
        <Icon type="plus" />
        <div className="ant-upload-text">上传</div>
      </div>
    );
 
  return (
    <Modal
      destroyOnClose
      title="新建Banner"
      visible={modalVisible}
      onOk={okHandle}
      onCancel={() => handleModalVisible()}
    >
      <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="展示图片">
              {form.getFieldDecorator('file', {
                valuePropName: 'file',
                getValueFromEvent: getValueFile,
                rules: [
                  {
                    required: true, message: '请上传Banner展示图片', type: 'object',
                  },
                ],
              })(
              <Upload 
                name="file" 
                action="http://upload-z2.qiniup.com" 
                listType="picture-card"
                fileList = {fileList}
                onChange={handleChange}
                data={token}
              >
              {fileList.length >= 1 ? null : uploadButton}
              </Upload>
              )}
            </FormItem>

      <FormItem labelCol={{ span: 5 }} wrapperCol={{ span: 15 }} label="Banner标题">
        {form.getFieldDecorator('title', {
          rules: [{ required: true, message: '请输入至少五个字符的标题描述！', min: 5 }],
        })(<Input placeholder="请输入" />)}
      </FormItem>
    </Modal>
  );
});

const action='banner';

/* eslint react/no-multi-comp:0 */
@connect(({ common, loading }) => ({
  common,
  loading: loading.models.common,
}))
@Form.create()
class TableList extends PureComponent {
  state = {
    fileList: [],
    modalVisible: false,
    updateModalVisible: false,
    expandForm: false,
    selectedRows: [],
    formValues: {},
    stepFormValues: {},
  };

  columns = [
    {
      title: '展示图片',
      dataIndex: 'src',
      render: (text, record) => (<img style={{width:'200px', height:'120px'}} src={record.src}/>)
    },
    {
      title: 'Banner标题',
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
          <a onClick={() =>this.confirmDelete(record)} >删除</a>
        </Fragment>
      ),
    },
  ];

  componentDidMount() {
    const { dispatch } = this.props;
    console.log(this.props);
    dispatch({
      type: 'common/fetch',
      payload: {action},
    });
  }

   getValueFile = ({ file }) => {
    if (file.status == 'done') return {status: file.status, uid: file.uid, fileName: file.response.key};
    if (file.status == 'error') return '';
    return file;
  };

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

  handleModalVisible = flag => {
    this.setState({
      modalVisible: !!flag,
      fileList: [],
    });
  };

  handleAdd = fields => {
    const { dispatch } = this.props;
    dispatch({
      type: 'common/add',
      payload: {action, ...fields, },
      callback: () => {
          message.success('添加成功');
      }
    });
    this.handleModalVisible();
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
  

  handleChange = ({fileList}) => {
    this.setState({ fileList });
  }

 
  render() {
    const {
      common: { data },
      loading,
    } = this.props;
    const { selectedRows, modalVisible, updateModalVisible, stepFormValues, fileList } = this.state;

    const parentMethods = {
      handleChange: this.handleChange,
      handleAdd: this.handleAdd,
      getValueFile: this.getValueFile,
      handleModalVisible: this.handleModalVisible,
    };
    const updateMethods = {
      handleUpdateModalVisible: this.handleUpdateModalVisible,
      handleUpdate: this.handleUpdate,
    };

  
    return (
      <PageHeaderWrapper>
        <Card bordered={false}>
          <div className={styles.tableList}>
            <div className={styles.tableListOperator}>
              <Button icon="plus" type="primary" onClick={() => this.handleModalVisible(true)}>
                新建
              </Button>
              {selectedRows.length > 0 && (
                <span>
                  <Button  onClick={() =>this.confirmDelete()} selectedKeys={[]}>批量删除</Button>
                </span>
              )}
            </div>
            <StandardTable 
              selectedRows={selectedRows}
              rowKey={record=>record.id}
              loading={loading}
              data={data}
              columns={this.columns}
              onSelectRow={this.handleSelectRows}
              onChange={this.handleStandardTableChange}
            />
          </div>
        </Card>
        <CreateForm {...parentMethods} token={data.token} fileList= {fileList} modalVisible={modalVisible} />
      </PageHeaderWrapper>
    );
  }
}

export default TableList;
