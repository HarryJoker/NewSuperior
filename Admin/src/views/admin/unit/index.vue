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
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        添加部门
      </el-button>
    </div>

    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%">
      <el-table-column width="150" align="center" label="部门Logo">
        <template slot-scope="scope">
          <el-image style="width: 100px; height: 100px" :src="'http://api.superior.yooar.com/uploads/' + scope.row.logo" />
        </template>
      </el-table-column>

      <el-table-column align="center" label="部门名称">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="部门用户">
        <template slot-scope="scope">
          <span v-for="item in scope.row.users" :key="item.id"> {{ userRules[item.rule] }} {{ item.account }}<br></span>
        </template>
      </el-table-column>

      <el-table-column align="center" label="分管领导">
        <template slot-scope="scope">
          <span>{{ makeLeaderUnitName(scope.row.parentid) }} <br> </span>
        </template>
      </el-table-column>

      <!-- <el-table-column align="center" width="130" label="操作">
        <template slot-scope="scope">
          <router-link :to="'/example/edit/'+scope.row.id">
            <el-button type="primary" size="small" icon="el-icon-edit">
              Edit
            </el-button>
          </router-link>
        </template>
      </el-table-column> -->
      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button size="mini" type="danger" @click="deleteData(row,$index)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- <pagination v-show="total>0" :total="total" :page.sync="listQuery.page" :limit.sync="listQuery.limit" @pagination="getList" /> -->
    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="dataForm" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <!-- <el-form-item label="Type" prop="type">
          <el-select v-model="temp.type" class="filter-item" placeholder="Please select">
            <el-option v-for="item in calendarTypeOptions" :key="item.key" :label="item.display_name" :value="item.key" />
          </el-select>
        </el-form-item> -->
        <!-- <el-form-item label="Date" prop="timestamp">
          <el-date-picker v-model="temp.timestamp" type="datetime" placeholder="Please pick a date" />
        </el-form-item> -->
        <el-form-item label="部门Logo">
          <el-upload
            class="avatar-uploader"
            action="http://api.superior.yooar.com/app.php/banner/uploadSingle"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon" />
          </el-upload>
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="temp.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门级别" prop="role">
          <el-select v-model="temp.role" class="filter-item" placeholder="请选择部门级别">
            <el-option v-for="item in unitRoles" :key="item.key" :label="item.display_name" :value="item.key" />
          </el-select>
        </el-form-item>
        <el-form-item label="分管领导" prop="parentid">
          <el-select v-model="temp.parentid" class="filter-item" placeholder="请选择分管领导">
            <el-option v-for="item in leaderUnits" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <!-- <el-form-item label="Status">
          <el-select v-model="temp.status" class="filter-item" placeholder="Please select">
            <el-option v-for="item in statusOptions" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item label="Imp">
          <el-rate v-model="temp.importance" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" :max="3" style="margin-top:8px;" />
        </el-form-item>
        <el-form-item label="Remark">
          <el-input v-model="temp.remark" :autosize="{ minRows: 2, maxRows: 4}" type="textarea" placeholder="Please input" />
        </el-form-item> -->
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
  </div>
</template>

<script>
import { fetchUnits, fetchLeaderUnits, createUnit, updateUnit, deleteUnit } from '@/api/unit'

const unitRoles = [
  { key: '1', display_name: '县长' },
  { key: '2', display_name: '副县长' },
  { key: '3', display_name: '督查室' },
  { key: '4', display_name: '部门' }
]

export default {
  name: 'UnitList',
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
      leaderUnits: null,
      listLoading: true,
      userRules: ['', '主要负责人:', '分管负责人:', '具体责任人:'],
      textMap: {
        update: '编辑更新',
        create: '添加部门'
      },
      unitRoles,
      imageUrl: '',
      temp: {
        id: undefined,
        title: '',
        content: ''
      },
      dialogStatus: '',
      dialogFormVisible: false,
      rules: {
        name: [{ required: true, message: '请填写部门名称', trigger: 'blur' }],
        parentid: [{ required: true, message: '请选择分管领导', trigger: 'blur' }],
        role: [{ required: true, message: '请选择部门级别', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getLeaderUnits()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchUnits().then(response => {
        this.list = response.data
        this.listLoading = false
      })
    },
    makeLeaderUnitName(leaderId) {
      var unit = this.leaderUnits.find(unit => unit.id === leaderId)
      return unit ? unit.name : '-'
    },
    getLeaderUnits() {
      this.listLoading = true
      fetchLeaderUnits().then(response => {
        this.leaderUnits = response.data
        this.getList()
      })
    },
    handleCreate() {
      // this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        console.log('clear event')
        this.$refs['dataForm'].clearValidate()
      })
    },
    createData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          console.log(this.temp)
          createUnit(this.temp).then(response => {
            this.list.unshift(response.data)
            this.dialogFormVisible = false
            this.imageUrl = ''
            this.$refs['dataForm'].resetFields()
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
    handleUpdate(row) {
      this.temp = Object.assign({}, row) // copy obj
      // this.temp.timestamp = new Date(this.temp.timestamp)
      this.imageUrl = 'http://api.superior.yooar.com/uploads/' + this.temp.logo
      this.dialogStatus = 'update'
      this.dialogFormVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const tempData = Object.assign({}, this.temp)
          updateUnit(tempData).then(() => {
            const index = this.list.findIndex(v => v.id === this.temp.id)
            this.list.splice(index, 1, this.temp)
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
    deleteData(row, index) {
      deleteUnit(row.id).then(() => {
        this.dialogFormVisible = false
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
        this.list.splice(index, 1)
      })
    },
    handleAvatarSuccess(res, file) {
      if (res.code === 0) {
        this.imageUrl = 'http://api.superior.yooar.com/uploads/' + res.data.file_name
        this.temp.logo = res.data.file_name
      }
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
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
