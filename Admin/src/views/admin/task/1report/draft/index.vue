<template>
  <div class="app-container">

    <div class="filter-container">
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        添加文稿
      </el-button>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column align="center" label="文稿名称">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="任务数量" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.taskCount }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="任务年份" width="150">
        <template slot-scope="scope">
          <span>{{ scope.row.taskYear }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button type="primary" size="mini" @click="handleEnter(row)">
            查看
          </el-button>
          <el-button size="mini" type="danger" @click="deleteData(row,$index)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <el-form-item label="文稿名称" prop="title">
          <el-input v-model="temp.name" placeholder="请填写文稿名称" />
        </el-form-item>
        <el-form-item label="任务年份" prop="taskYear">
          <el-select v-model="temp.taskYear" class="filter-item" placeholder="请选择任务年份">
            <el-option v-for="item in years" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">
          Cancel
        </el-button>
        <el-button type="primary" @click="dialogStatus==='create' ? createData() : updateData()">
          Confirm
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { fetchList, createDraft, deleteDraft } from '@/api/draft'

export default {
  name: 'DraftList',
  data() {
    return {
      category: this.$route.path.split('=')[1],
      list: null,
      listLoading: true,
      textMap: {
        update: 'Edit',
        create: 'Create'
      },
      years: [new Date().getFullYear(), new Date().getFullYear() + 1],
      dialogStatus: '',
      temp: {
        id: undefined,
        name: ''
      },
      dialogFormVisible: false,
      rules: {
        name: [{ required: true, message: '请填写文稿名称', trigger: 'blur' }],
        taskYear: [{ required: true, message: '请填任务调度年份', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchList(this.category).then(response => {
        this.list = response.data
        this.listLoading = false
      })
    },
    handleCreate() {
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.temp.category = 1
          createDraft(this.temp).then((response) => {
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
    handleEnter(row) {
      console.log(row.id)
      this.$router.push({ name: 'draftTasks', params: { draftId: row.id, taskYear: row.taskYear, category: this.category }})
    },
    deleteData(row, index) {
      deleteDraft(row.id).then(() => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
        this.list.splice(index, 1)
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
