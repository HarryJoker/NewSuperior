<template>
  <div class="app-container">
    <upload-excel-component :on-success="handleSuccess" :before-upload="beforeUpload" style="margin-bottom: 10px;" />
    <el-button v-if="selectTasks.length > 0" class="filter-item" size="mini" type="primary" icon="el-icon-check" @click="handleDeploySelect">
      发布
    </el-button>
    <el-button v-if="selectTasks.length > 0" class="filter-item" size="mini" style="margin-left: 10px;" type="primary" icon="el-icon-delete" @click="handleDeleteSelect">
      删除
    </el-button>
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="35" />
      <el-table-column width="80" align="center" label="序号">
        <template slot-scope="scope">
          <span>{{ scope.row.taskSerial }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="重点工作" :show-overflow-tooltip="true" width="220">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="工作计划" :show-overflow-tooltip="true" width="280">
        <template slot-scope="scope">
          <span>{{ scope.row.content }}</span>
        </template>
      </el-table-column>

      <el-table-column label="责任部门" align="center">
        <template slot-scope="scope">{{ scope.row.unitNames }}</template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="260" class-name="small-padding fixed-width">
        <template slot-scope="{row, $index}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button type="primary" size="mini" @click="handleDeploy(row, $index)">
            发布
          </el-button>
          <el-button size="mini" type="danger" @click="deleteData(row, $index)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <el-form-item label="任务类型">
          <div class="term">政府工作报告</div>
        </el-form-item>
        <el-form-item label="任务序号" prop="taskSerial">
          <el-input v-model="temp.taskSerial" placeholder="请输入任务序号" />
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
          <el-input v-model="temp.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="推进计划" prop="content">
          <el-input v-model="temp.content" type="textarea" :rows="8" placeholder="请输入消息内容" />
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
          <el-select v-model="temp.unitIds" multiple filterable allow-create default-first-option class="filter-item" placeholder="请选择责任部门" @change="handleChangeUnits">
            <el-option v-for="item in unitis" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="附件图片">
          <el-upload
            class="upload-demo"
            action="http://api.superior.yooar.com/app.php/banner/uploadSingle"
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :on-success="handleImageSuccess"
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
        <el-button type="primary" @click="dialogStatus==='create' ? createData() : updateData()">
          确认
        </el-button>
      </div>
    </el-dialog>

    <el-dialog :visible.sync="dialogImageVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </div>
</template>

<script>
import UploadExcelComponent from '@/components/UploadExcel/index.vue'
import { fetchList, createDraftTasks, updateDraftTask, deleteDraftTask, deleteDraftTasks, deployDraftTask, deployDraftTasks } from '@/api/drafttask'
import { fetchCommonUnits, getMapCommonUnits } from '@/api/unit'

export default {
  name: 'UploadExcel',
  components: { UploadExcelComponent },
  data() {
    return {
      category: this.$route.params.category,
      listLoading: false,
      list: null,
      draftId: this.$route.params.draftId,
      taskYear: this.$route.params.taskYear,
      keyMap: { '任务序号': 'taskSerial', '任务类别': 'taskLabel', '重点工作': 'title', '推进计划': 'content', '责任部门': 'unitNames' },
      textMap: {
        update: '编辑更新',
        create: '发布任务'
      },
      fileList: [],
      attachmentUrl: 'http://api.superior.yooar.com/uploads/',
      unitis: null,
      temp: {},
      selectTasks: [],
      dialogStatus: '',
      dialogFormVisible: false,
      dialogImageVisible: false,
      dialogImageUrl: '',
      years: [new Date().getFullYear(), new Date().getFullYear() + 1],
      taskTypes: ['预期目标类', '试点验收类', '定量类', '定性类'],
      rules: {
        taskSerial: [{ required: true, message: '请填写任务序号', trigger: 'blur' }],
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
    console.log(this.$route.params.category)
  },
  methods: {
    beforeUpload(file) {
      this.listLoading = true
      const isLt1M = file.size / 1024 / 1024 < 1

      if (isLt1M) {
        return true
      }

      this.$message({
        message: 'Please do not upload files larger than 1m in size.',
        type: 'warning'
      })
      return false
    },
    handleSuccess({ results, header }) {
      results.forEach(element => {
        for (var key in element) {
          var newKey = this.keyMap[key]
          if (newKey) {
            element[newKey] = element[key]
            delete element[key]
          }
        }
      })
      this.makeParseTaskUnit(results)
    },
    makeParseTaskUnit(tasks) {
      this.listLoading = true
      getMapCommonUnits().then(response => {
        tasks.forEach(element => {
          element.category = this.category
          element.draftId = this.draftId
          element.reportCycle = 1
          element.reportDate = 5
          element.taskYear = this.taskYear
          console.log(element.unitNames)
          element.unitNames = element.unitNames.replace('\\s', '')
          element.unitIds = this.makeTaskUnitIds(element.unitNames, response.data)
        })
        this.createDraftTasks(tasks)
      })
    },
    makeTaskUnitIds(unitNames, mapUnits) {
      var unitIds = ''
      unitNames.split(',').forEach(element => {
        if (Object.prototype.hasOwnProperty.call(mapUnits, element)) {
          unitIds += unitIds.length > 0 ? ',' : ''
          unitIds += mapUnits[element].id
        }
      })
      return unitIds
    },
    createDraftTasks(tasks) {
      createDraftTasks(tasks).then(response => {
        this.getList()
        this.$notify({
          title: 'Success',
          message: 'Import Successfully',
          type: 'success',
          duration: 2000
        })
      })
    },
    getCommonUnits() {
      this.listLoading = true
      fetchCommonUnits().then(response => {
        this.unitis = response.data
        this.getList()
      })
    },
    getList() {
      this.listLoading = true
      fetchList(this.draftId).then(response => {
        this.list = response.data
        this.listLoading = false
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
    handleChangeUnits(val) {
      var unitNames = ''
      this.unitis.forEach(element => {
        if (val.includes(element.id)) {
          unitNames += unitNames.length > 0 ? ',' : ''
          unitNames += element.name
        }
      })
      this.temp.unitNames = unitNames
    },
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      var files = []
      if (this.temp.attachmentids.length > 0) {
        this.temp.attachmentids.split(',').forEach(element => {
          files.push({ 'name': element, 'url': this.attachmentUrl + element })
        })
      }
      this.fileList = files
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var tempData = Object.assign({}, this.temp)
          tempData.unitIds = tempData.unitIds.join(',')
          tempData.attachmentids = this.makeAttachmentIds()
          updateDraftTask(tempData).then((response) => {
            const index = this.list.findIndex(v => v.id === this.temp.id)
            this.list.splice(index, 1, response.data)
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: 'Update Successfully',
              type: 'success',
              duration: 2000
            })
          })
        }
      })
    },
    handleDeploy(row, index) {
      this.listLoading = true
      deployDraftTask(row.id).then(response => {
        this.$notify({
          title: 'Success',
          message: 'Deploy Successfully',
          type: 'success',
          duration: 2000
        })
        this.list.splice(index, 1)
        this.listLoading = false
      })
    },
    deleteData(row, index) {
      this.listLoading = true
      deleteDraftTask(row.id).then((response) => {
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
        this.list.splice(index, 1)
        this.listLoading = false
      })
    },
    handleRemove(file, fileList) {
      this.fileList = fileList
    },
    handlePreview(file) {
      this.dialogImageUrl = file.url
      this.dialogImageVisible = true
    },
    handleImageSuccess(response, file, fileList) {
      var url = this.attachmentUrl + response.data.file_name
      this.fileList.push({ 'name': response.data.file_name, 'url': url })
    },
    handleSelectionChange(rows) {
      this.selectTasks = rows
      console.log(rows)
    },
    handleDeploySelect() {
      this.listLoading = true
      var draftIds = this.selectTasks.map(element => {
        return element.id
      })
      deployDraftTasks({ 'ids': draftIds }).then(response => {
        this.$notify({
          title: 'Success',
          message: 'Deploy Successfully',
          type: 'success',
          duration: 2000
        })
        this.getList()
      })
    },
    handleDeleteSelect() {
      this.listLoading = true
      var draftIds = this.selectTasks.map(element => {
        return element.id
      })
      deleteDraftTasks({ 'ids': draftIds }).then(response => {
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
        this.getList()
      })
    }
  }
}
</script>
