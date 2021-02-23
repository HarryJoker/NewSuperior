<template>
  <div class="app-container">
    <div class="filter-container">
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        添加
      </el-button>
    </div>
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="重点工作">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="具体描述">
        <template slot-scope="scope">
          <span>{{ scope.row.content }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="问题类型">
        <template slot-scope="scope">
          <span>{{ scope.row.taskLabel }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="责任单位">
        <template slot-scope="scope">
          <span>{{ scope.row.unitTasks[0].unitName }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" width="150" label="督查公开">
        <template slot-scope="scope">
          <span>{{ statusMap[scope.row.unitTasks[0].status] }}</span>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog title="添加公开" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" v-loading="dialogLoading" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <el-form-item label="重点工作" prop="title">
          <el-input v-model="temp.title" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="具体描述" prop="content">
          <el-input v-model="temp.content" type="textarea" :rows="8" placeholder="请输入消息内容" />
        </el-form-item>
        <el-form-item label="责任部门" prop="unitIds">
          <el-select v-model="temp.unitIds" multiple filterable allow-create default-first-option class="filter-item" placeholder="请选择责任部门">
            <el-option v-for="item in unitis" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="督查公开" prop="status">
          <el-radio v-model="temp.status" label="72">落后榜</el-radio>
          <el-radio v-model="temp.status" label="73">担当榜</el-radio>
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
  </div>
</template>

<script>
import { createTask, fetchGovermentPublicTaskList } from '@/api/task'
import { fetchCommonUnits } from '@/api/unit'

export default {
  name: 'PublicList',
  data() {
    return {
      list: null,
      listLoading: true,
      dialogLoading: false,
      dialogFormVisible: false,
      unitis: null,
      temp: {},
      statusMap: {
        72: '落后榜',
        73: '担当榜'
      },
      rules: {
        status: [{ required: true, message: '请选择督查公开', trigger: 'blur' }],
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
      fetchGovermentPublicTaskList().then(response => {
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
          this.temp.category = 11
          createTask(this.temp).then((response) => {
            this.dialogFormVisible = false
            this.$notify({
              title: 'Success',
              message: 'Created Successfully',
              type: 'success',
              duration: 2000
            })
            this.getList()
          })
        }
      })
    },
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
