<template>
  <div class="app-container">

    <div class="filter-container">
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        发布版本
      </el-button>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="ID序号">
        <template slot-scope="scope">
          <span>{{ scope.row.id }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="版本号">
        <template slot-scope="scope">
          <span>{{ scope.row.new_version }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="下载地址">
        <template slot-scope="scope">
          <span>{{ scope.row.apk_file_url }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="更新内容">
        <template slot-scope="scope">
          <span>{{ scope.row.update_log }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="Apk包大小">
        <template slot-scope="scope">
          <span>{{ scope.row.target_size }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="更新方式">
        <template slot-scope="scope">
          <span>{{ constraintTxts[scope.row.constraint] }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="发布时间">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="Create" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <el-form-item label="上传新包" prop="apk_file_url">
          <el-upload
            class="upload-demo"
            action="http://api.anter.yooar.com/app.php/version/uploadSingle"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :on-success="handleSuccess"
            :before-remove="beforeRemove"
            multiple
            :limit="1"
            :on-exceed="handleExceed"
            :file-list="fileList"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传Apk文件</div>
          </el-upload>
          <el-input v-model="temp.apk_file_url" type="hidden" style="height: 0px;" />
        </el-form-item>
        <el-form-item label="版本号" prop="new_version">
          <el-input v-model="temp.new_version" placeholder="请填写版本号" />
        </el-form-item>
        <el-form-item label="更新内容" prop="update_log">
          <el-input v-model="temp.update_log" rows="10" type="textarea" placeholder="请输入更新内容" />
        </el-form-item>
        <el-form-item label="Apk包大小" prop="target_size">
          <el-input v-model="temp.target_size" placeholder="请输入APk包大小" />
        </el-form-item>
        <el-form-item label="更新方式" prop="constraint">
          <template>
            <el-radio v-model="temp.constraint" label="0">提示更新</el-radio>
            <el-radio v-model="temp.constraint" label="1">强制更新</el-radio>
          </template>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="createData()">
          Confirm
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, createVersion } from '@/api/version'

export default {
  name: 'VersionList',
  data() {
    return {
      list: null,
      listLoading: true,
      temp: { constraint: '1' },
      fileList: [],
      constraintTxts: ['提示更新', '强制更新'],
      dialogFormVisible: false,
      attachmentUrl: 'http://api.anter.yooar.com/uploads/',
      rules: {
        apk_file_url: [{ required: true, message: '请上传新版本安装包', trigger: 'blur' }],
        new_version: [{ required: true, message: '请填写新版本号', trigger: 'blur' }],
        target_size: [{ required: true, message: '请填写新版本包大小', trigger: 'blur' }],
        constraint: [{ required: true, message: '请选择新版本发布方式', trigger: 'blur' }],
        update_log: [{ required: true, message: '请填写该版本更新内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList().then(response => {
        this.list = response.data
        this.listLoading = false
      })
    },
    handleCreate() {
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          createVersion(this.temp).then(response => {
            this.list.unshift(response.data)
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: 'Created Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleSuccess(response, file, fileList) {
      var url = this.attachmentUrl + response.data.file_name
      this.temp['apk_file_url'] = url
      console.log('handleSuccess:' + url)
    },
    handleRemove(file, fileList) {
      console.log(file, fileList)
    },
    handlePreview(file) {
      console.log(file)
    },
    handleExceed(files, fileList) {
      this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
    },
    beforeRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}?`)
    }
  }
}
</script>

<style scoped>
.edit-input {
  padding-right: 100px;
}
.cancel-btn {
  position: absolute;
  right: 15px;
  top: 10px;
}
.avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }
  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }
</style>
