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
      <el-table-column align="center" label="投票事项">
        <template slot-scope="scope">
          <span>{{ scope.row.content }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="当前排名">
        <template slot-scope="scope">
          <span>{{ scope.row.rank }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="投票数量">
        <template slot-scope="scope">
          <span>{{ scope.row.voteCount }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="投票比例">
        <template slot-scope="scope">
          <span>{{ scope.row.ratio }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" width="150" label="状态">
        <template slot-scope="scope">
          <span>{{ opinionStatus[scope.row.status] }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button v-show="row.status == 3" type="primary" size="mini" @click="handleOpenVote(row,$index)">
            开启投票
          </el-button>
          <el-button v-show="row.status == 4" size="mini" type="danger" @click="handleCloseVote(row,$index)">
            停止投票
          </el-button>
          <el-button v-show="row.status == 5" size="mini" type="primary" @click="handleScheduleVote(row,$index)">
            开启督办
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog title="Edit" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" v-loading="dialogLoading" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <el-form-item label="任务类型">
          <div class="term">民生在线</div>
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
          <el-input v-model="temp.title" placeholder="请输入重点工作" />
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
          取消
        </el-button>
        <el-button type="primary" @click="createData()">
          确定
        </el-button>
      </div>
    </el-dialog>
    <el-dialog :visible.sync="dialogImageVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>

<script>
import { updateOpinion } from '@/api/opinion'
import { fetchList } from '@/api/vote'
import { createTask } from '@/api/task'
import { fetchCommonUnits } from '@/api/unit'

export default {
  name: 'ClueList',
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
      dialogLoading: false,
      opinionStatus: ['未审核', '未通过', '已通过', '待投票', '投票中', '投票结束', '已调度'],
      temp: {},
      unitis: null,
      years: [new Date().getFullYear(), new Date().getFullYear() + 1],
      taskTypes: ['城市基础设施问题', '教育卫生问题', '经济发展问题', '道路提升问题', '文化旅游问题'],
      dialogFormVisible: false,
      fileList: [],
      attachmentUrl: 'http://api.superior.yooar.com/uploads/',
      dialogImageUrl: '',
      dialogImageVisible: false,
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
    handleOpenVote(row, index) {
      // this.temp = Object.assign({}, row) // copy obj
      var tempData = Object.assign({}, row)
      delete tempData['opinionId']
      delete tempData['rank']
      delete tempData['ratio']
      delete tempData['clueCount']
      tempData.status = 4
      this.listLoading = true
      updateOpinion(tempData).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Update Successfully',
          type: 'success',
          duration: 2000
        })
        this.list[index].status = 4
        this.listLoading = false
      })
    },
    handleCloseVote(row, index) {
      var tempData = Object.assign({}, row)
      delete tempData['opinionId']
      delete tempData['rank']
      delete tempData['ratio']
      delete tempData['clueCount']
      tempData.status = 5
      updateOpinion(tempData).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Update Successfully',
          type: 'success',
          duration: 2000
        })
        this.list[index].status = 5
        this.listLoading = false
      })
    },
    handleScheduleVote(row, index) {
      var tempData = []
      console.log(row)
      tempData.opinionId = row.id
      tempData.category = 8
      tempData.taskLabel = this.taskTypes[row.category]
      tempData.labelType = row.category
      tempData.taskYear = new Date().getFullYear()
      tempData.title = ''
      tempData.content = row.content
      tempData.unitIds = []
      tempData.reportCycle = 0
      tempData.reportDate = ''
      this.fileList = []
      this.temp = Object.assign({}, tempData)
      this.dialogFormVisible = true
      console.log(this.temp)
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
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
      var tempData = { 'id': this.temp.opinionId, 'status': 6 }
      updateOpinion(tempData).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Schedule Successfully',
          type: 'success',
          duration: 2000
        })
        this.list.forEach(element => {
          if (element.id === tempData.id) {
            element.status = 6
          }
        })
        this.dialogLoading = false
        this.dialogFormVisible = false
      })
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
