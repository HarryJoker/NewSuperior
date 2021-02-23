<template>
  <div class="app-container">

    <div class="filter-container">
      <!-- <el-input v-model="listQuery.title" placeholder="Title" style="width: 200px;" class="filter-item" @keyup.enter.native="handleFilter" />
      <el-select v-model="listQuery.importance" placeholder="Imp" clearable style="width: 90px" class="filter-item">
        <el-option v-for="item in importanceOptions" :key="item" :label="item" :value="item" />
      </el-select>
      <el-select v-model="listQuery.type" placeholder="Type" clearable class="filter-item" style="width: 130px">
        <el-option v-for="item in calendarTypeOptions" :key="item.key" :label="item.display_name+'('+item.key+')'" :value="item.key" />
      </el-select>
      <el-select v-model="listQuery.sort" style="width: 140px" class="filter-item" @change="handleFilter">
        <el-option v-for="item in sortOptions" :key="item.key" :label="item.label" :value="item.key" />
      </el-select>
      <el-button v-waves class="filter-item" type="primary" icon="el-icon-search" @click="handleFilter">
        Search
      </el-button> -->
      <!-- <el-button v-waves :loading="downloadLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleDownload">
        Export
      </el-button>
      <el-checkbox v-model="showReviewer" class="filter-item" style="margin-left:15px;" @change="tableKey=tableKey+1">
        reviewer
      </el-checkbox> -->
      <!-- <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        Add
      </el-button> -->
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="标题">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="描述">
        <template slot-scope="scope">
          <span>{{ scope.row.content }}</span>
        </template>
      </el-table-column>

      <el-table-column width="150" align="center" label="图片">
        <template slot-scope="scope">
          <el-image v-for="(image, index) in scope.row.attachments" :key="index" style="width: 100px; height: 100px" :src="'http://api.superior.yooar.com/uploads/' + image" />
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="提报人">
        <template slot-scope="scope">
          <span>{{ scope.row.userName }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="手机号码">
        <template slot-scope="scope">
          <span>{{ scope.row.phone }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="180" label="提报时间">
        <template slot-scope="scope">
          <span>{{ scope.row.createtime }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" width="150" label="状态">
        <template slot-scope="scope">
          <span>{{ culeStatus[scope.row.status] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button v-show="row.status == 0" type="primary" size="mini" @click="handleInvalid(row,$index)">
            不通过
          </el-button>
          <el-button v-show="row.status == 0" type="primary" size="mini" @click="handleThrough(row,$index)">
            通过
          </el-button>
          <el-button v-show="row.status == 2" size="mini" type="danger" @click="handleScheduleClue(row,$index)">
            督办
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="Edit" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" v-loading="dialogLoading" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <el-form-item label="任务类型">
          <div class="term">线索督查</div>
        </el-form-item>
        <el-form-item label="任务年份" prop="taskYear">
          <el-select v-model="temp.taskYear" class="filter-item" placeholder="请选择任务年份">
            <el-option v-for="item in years" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务类别" prop="taskLabel">
          <el-select v-model="temp.taskLabel" class="filter-item" placeholder="请选择任务类别">
            <el-option v-for="(item, index) in taskTypes" :key="index" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="重点工作" prop="title">
          <el-input v-model="temp.title" placeholder="请输入线索标题" />
        </el-form-item>
        <el-form-item label="具体内容" prop="content">
          <el-input v-model="temp.content" type="textarea" :rows="8" placeholder="请输入具体内容" />
        </el-form-item>
        <el-form-item label="调度周期" prop="reportCycle">
          <el-radio v-model="temp.reportCycle" label="1">月调度</el-radio>
          <el-radio v-model="temp.reportCycle" label="2">具体时间</el-radio>
        </el-form-item>
        <el-form-item v-if="temp.reportCycle == 1" label="报送时间" prop="reportDate">
          <el-input v-model="temp.reportDate" placeholder="请输每月报送日" />
        </el-form-item>
        <el-form-item v-if="temp.reportCycle == 2" label="报送时间" prop="reportDate">
          <el-date-picker v-model="temp.reportDate" type="date" placeholder="请输报送日期" />
        </el-form-item>
        <el-form-item label="责任部门" prop="unitIds">
          <el-select v-model="temp.unitIds" multiple filterable allow-create default-first-option class="filter-item" placeholder="请选择责任部门">
            <el-option v-for="item in unitis" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="附件图片">
          <el-upload
            ref="imageUpload"
            class="upload-demo"
            action="http://api.superior.yooar.com/app.php/banner/uploadSingle"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :on-success="handleSuccess"
            :file-list="fileList"
            list-type="picture-card"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div>
          </el-upload>
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
    <el-dialog :visible.sync="dialogImageVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, updateClue } from '@/api/clue'
import { createTask } from '@/api/task'
import { fetchCommonUnits } from '@/api/unit'

export default {
  name: 'OpinionList',
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'info',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: null,
      listLoading: true,
      culeStatus: ['未审核', '不通过', '已通过', '已督办'],
      temp: {},
      curClueId: null,
      dialogStatus: '',
      dialogFormVisible: false,
      dialogLoading: false,
      dialogImageVisible: false,
      unitis: null,
      years: [new Date().getFullYear(), new Date().getFullYear() + 1],
      taskTypes: ['关于政府效能问题', '个人办事问题'],
      fileList: [],
      attachmentUrl: 'http://api.superior.yooar.com/uploads/',
      dialogImageUrl: '',
      rules: {
        taskYear: [{ required: true, message: '请选择任务年度', trigger: 'blur' }],
        taskLabel: [{ required: true, message: '请选择任务类型', trigger: 'blur' }],
        reportCycle: [{ required: true, message: '请选择调度周期', trigger: 'blur' }],
        reportDate: [{ required: true, message: '请填写报送时间', trigger: 'blur' }],
        unitIds: [{ required: true, message: '请选择责任部门', trigger: 'blur' }],
        title: [{ required: true, message: '请填写标题', trigger: 'blur' }],
        content: [{ required: true, message: '请填写消息内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getCommonUnits()
  },
  methods: {
    getCommonUnits() {
      this.listLoading = true
      fetchCommonUnits().then(response => {
        this.unitis = response.data
        this.getList()
      })
    },
    getList() {
      this.listLoading = true
      fetchList().then(response => {
        this.list = response.data
        this.listLoading = false
      })
    },
    handleThrough(row, index) {
      var tempData = Object.assign({}, row)
      tempData.status = 2
      this.listLoading = true
      updateClue(tempData).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Verify Successfully',
          type: 'success',
          duration: 2000
        })
        this.list[index].status = 2
        this.listLoading = false
      })
    },
    handleInvalid(row, index) {
      var tempData = Object.assign({}, row)
      tempData.status = 1
      updateClue(tempData).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Verify Successfully',
          type: 'success',
          duration: 2000
        })
        this.list[index].status = 1
        this.listLoading = false
      })
    },
    handleScheduleClue(row, index) {
      var tempData = []
      tempData.category = 9
      tempData.taskLabel = this.taskTypes[row.category]
      tempData.taskYear = new Date().getFullYear()
      tempData.title = row.title
      tempData.content = row.content
      tempData.unitIds = []
      tempData.reportCycle = 0
      tempData.reportDate = ''
      this.curClueId = row.id
      this.fileList = []
      this.temp = Object.assign({}, tempData)
      this.dialogFormVisible = true
      console.log(this.temp)
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.dialogLoading = true
          this.temp.attachmentids = this.makeAttachmentIds()
          createTask(this.temp).then((response) => {
            this.updateScheduleStatus()
          })
        }
      })
    },
    makeAttachmentIds() {
      var attachmentids = ''
      if (this.fileList.length > 0) {
        this.fileList.forEach(element => {
          attachmentids += attachmentids.length > 0 ? ',' : ''
          attachmentids += element.name
        })
      }
      return attachmentids
    },
    updateScheduleStatus() {
      var tempData = { 'id': this.curClueId, 'status': 3 }
      updateClue(tempData).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Schedule Successfully',
          type: 'success',
          duration: 2000
        })
        this.list.forEach(element => {
          if (element.id === tempData.id) {
            element.status = 3
          }
        })
        this.curClueId = ''
        this.dialogLoading = false
        this.dialogFormVisible = false
      })
    },
    handleRemove(file, fileList) {
      this.fileList = fileList
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url
      this.dialogImageVisible = true
    },
    handleSuccess(response, file, fileList) {
      var url = this.attachmentUrl + response.data.file_name
      this.fileList.push({ 'name': response.data.file_name, 'url': url })
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
