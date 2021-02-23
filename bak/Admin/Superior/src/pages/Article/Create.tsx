
import React, { PureComponent } from 'react';

import * as qiniu from 'qiniu-js'

import PageHeaderWrapper from '@/components/PageHeaderWrapper';

import { Card, Input, Form, Button, Divider ,message, } from 'antd';

import { connect } from 'dva';

import BraftEditor from 'braft-editor'

import 'braft-editor/dist/index.css'

import styles from './Create.less';

const FormItem = Form.Item;

const rawString = `{"blocks":[{"key":"9hu83","text":"Hello World!","type":"unstyled","depth":0,"inlineStyleRanges":[{"offset":6,"length":5,"style":"BOLD"},{"offset":6,"length":5,"style":"COLOR-F32784"}],"entityRanges":[],"data":{}}],"entityMap":{}}`

@connect(({ article, loading }) => ({
  article,
  loading: loading.models.article,
}))
@connect(({ common, loading }) => ({
  common,
  loading: loading.models.common,
}))
@Form.create()
class Editor extends PureComponent {

  state = {
    // 创建一个空的editorState作为初始值
    editorState: BraftEditor.createEditorState(null)
  }

  async componentDidMount() {
    // 假设此处从服务端获取html格式的编辑器内容
    // const htmlContent = await fetchEditorContent()
    // 使用BraftEditor.createEditorState将html字符串转换为编辑器需要的editorStat
    // this.setState({
    //   editorState: BraftEditor.createEditorState(rawString)
    // })

    console.log(this.props)
    console.log('Component DID MOUNT!')

    const { dispatch } = this.props;
    console.log(this.props);
    dispatch({
      type: 'common/fetch',
      payload: {action: "media"},
      callback: () => {
        // this.setState({
        //   editorState: BraftEditor.createEditorState(rawString)
        // })

        this.props.form.setFieldsValue({
          content: BraftEditor.createEditorState('<p>Hello <b>World!</b></p>')
        })
      },
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


  uploadFn = (param) => {

    const { dispatch, common:{data: {token}} } = this.props;

    const putExtra = {
    }
    const config = {
    }
    const observer = {
      saveMediaUrl(url) {
        console.log(url);
        dispatch({
          type: 'common/add',
          payload: {action: "media", url: url },
          callback: () => {
              message.success('添加成功');
          }
        });
      },
      next(res) {
        param.progress(res.total.percent)
      },
      error(err) {
        param.error({
          msg: err.message
        })
      },
      complete(res) {
        //上传成功,保存url
        observer.saveMediaUrl('http://img.yooar.com/' + res.key)
        param.success({
          url: 'http://img.yooar.com/' + res.key
        })
      }
    }
    qiniu.upload(param.file, param.name,  token ? token.token : '', putExtra, config).subscribe(observer)
  }

  submitContent = (event) => {
    // 在编辑器获得焦点时按下ctrl+s会执行此方法
    // 编辑器内容提交到服务端之前，可直接调用editorState.toHTML()来获取HTML格式的内容
    // const htmlContent = this.state.editorState.toHTML()
    // const result = await saveEditorContent(htmlContent)
    // console.log('save content: ' + htmlContent)

    // console.log( this.state.editorState.toRAW())
    // dispatch({
    //   type: 'article/add',
    //   payload: {action: 'article', title: rawContent.blocks[0].text, cover: rawContent.entityMap[0].data.url, html: htmlContent, },
    //   callback: () => {
    //       message.success('添加成功');
    //   }
    // });


    event.preventDefault()
    const { dispatch } = this.props;
    this.props.form.validateFields((error, values) => {
      if (!error) {
        const rawContent = values.content.toRAW(true);
        console.log(rawContent);
        let cover = ''
        Object.values(rawContent.entityMap).forEach(element => {
          if (element.type == 'IMAGE') {
            cover = element.data.url
            return
          }
        });
        dispatch({
          type: 'article/add',
          payload: {action: 'article', title: values.title, cover: cover, html: values.content.toHTML(), },
          callback: () => {
              message.success('添加成功');
          }
        });
      }
    })

  }

  handleEditorChange = (editorState) => {
    this.setState({ editorState })
  }

  preview = () => {

    if (window.previewWindow) {
      window.previewWindow.close()
    }

    window.previewWindow = window.open()
    window.previewWindow.document.write(this.buildPreviewHtml())
    window.previewWindow.document.close()
  }

  buildPreviewHtml() {

    return `
      <!Doctype html>
      <html>
        <head>
          <title>Preview Content</title>
          <style>
            html,body{
              height: 100%;
              margin: 0;
              padding: 0;
              overflow: auto;
              background-color: #f1f2f3;
            }
            .container{
              box-sizing: border-box;
              width: 1000px;
              max-width: 100%;
              min-height: 100%;
              margin: 0 auto;
              padding: 30px 20px;
              overflow: hidden;
              background-color: #fff;
              border-right: solid 1px #eee;
              border-left: solid 1px #eee;
            }
            .container img,
            .container audio,
            .container video{
              max-width: 100%;
              height: auto;
            }
            .container p{
              white-space: pre-wrap;
              min-height: 1em;
            }
            .container pre{
              padding: 15px;
              background-color: #f1f1f1;
              border-radius: 5px;
            }
            .container blockquote{
              margin: 0;
              padding: 15px;
              background-color: #f1f1f1;
              border-left: 3px solid #d1d1d1;
            }
          </style>
        </head>
        <body>
          <div class="container">${this.state.editorState.toHTML()}</div>
        </body>
      </html>
    `

  }

  render() {

    const { loading, common: {data:{list}}, form: { getFieldDecorator, getFieldValue }} = this.props;

    console.log(list)

    const extendControls = [
      {
        key: 'custom-button',
        type: 'button',
        text: '预览',
        onClick: this.preview
      }
    ]

    const formItemLayout = {
      labelCol: {
        xs: { span: 10 },
        sm: { span: 2 },
      },
      wrapperCol: {
        xs: { span: 14 },
        sm: { span: 21 },
        // md: { span: 10 },
      },
    };

    return (
      
      <PageHeaderWrapper>
        <Card bordered={true} size="small" style={{ marginLeft: 25, marginRight: 25, padding: 15 }}>

          <div className="demo-container">
            {/* <BraftEditor
              value={editorState}
              onChange={this.handleEditorChange}
              extendControls={extendControls}
              onSave={this.submitContent}
              media={{items: list, uploadFn: this.uploadFn }}
            /> */}
         

          {/* <Divider></Divider>
          <Button type="primary"  loading={loading} onClick={() =>this.submitContent()}>
            提交
          </Button> */}


          <Form onSubmit={this.submitContent}>
          <FormItem {...formItemLayout} label="文章标题">
            {getFieldDecorator('title', {
              rules: [{
                required: true,
                message: '请输入标题',
              }],
            })(
              <Input size="large" placeholder="请输入标题"/>
            )}
          </FormItem>
          <FormItem {...formItemLayout} label="文章正文">
            {getFieldDecorator('content', {
              validateTrigger: 'onBlur',
              rules: [{
                required: true,
                validator: (_, value, callback) => {
                  if (value.isEmpty()) {
                    callback('请输入正文内容')
                  } else {
                    callback()
                  }
                }
              }],
            })(
              <BraftEditor
                className="my-container"
                placeholder="请输入正文内容"
                extendControls={extendControls}
                media={{items: list, uploadFn: this.uploadFn }}
              />
            )}
          </FormItem>
          <FormItem {...formItemLayout}>
            <Button size="large" type="primary" htmlType="submit">提交</Button>
          </FormItem>
        </Form>
        </div>

        </Card>
      </PageHeaderWrapper>
    )
  }
}

export default Editor;