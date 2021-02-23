<template>
  <div class="app-container">

    <div class="filter-container">
      <el-select v-model="listQuery.status" placeholder="选择任务当前状态" clearable style="width: 180px" class="filter-item">
        <el-option v-for="(item, index) in verifyStatus" :key="category + index" :label="item.label" :value="item.value" />
      </el-select>
      <el-select v-model="listQuery.leaderUnitId" placeholder="选择分管领导" clearable class="filter-item" style="width: 150px; margin-left: 10px;">
        <el-option v-for="item in leaderUnits" :key="category + item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-button class="filter-item" type="primary" icon="el-icon-search" style="margin-left: 10px;" @click="handleFilter">
        搜索
      </el-button>
      <el-button class="filter-item" style="margin-left: 10px;" type="primary" icon="el-icon-edit" @click="handleCreate">
        发布
      </el-button>
      <el-button :loading="exportLoading" class="filter-item" type="primary" icon="el-icon-download" @click="handleExport">
        导出
      </el-button>
      <!-- <el-checkbox v-model="showReviewer" class="filter-item" style="margin-left:15px;" @change="tableKey=tableKey+1">
        reviewer
      </el-checkbox> -->
    </div>

    <el-button v-if="selectTasks.length > 0" class="filter-item" size="mini" type="primary" icon="el-icon-delete" @click="handleDeleteSelect">
      删除选中项
    </el-button>
    <el-table v-loading="listLoading" :data="list" border fit highlight-current-row style="width: 100%" :row-class-name="getRowClassName" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="35" />
      <el-table-column type="expand" label="#">
        <template slot-scope="props">
          <el-table
            v-if="props.row.unitTasks.length > 1"
            border
            fit
            :data="props.row.unitTasks"
            style="width: 100%;"
          >
            <el-table-column label="责任部门" align="center">
              <template slot-scope="scope">{{ scope.row.unitName }}</template>
            </el-table-column>
            <el-table-column label="分管领导" align="center">
              <template slot-scope="scope">{{ scope.row.unitParentName }}</template>
            </el-table-column>
            <el-table-column label="报送进度" align="center">
              <template slot-scope="scope">
                <el-progress :percentage="parseInt(scope.row.reportProgress)" color="#409eff" />
              </template>
            </el-table-column>
            <el-table-column label="完成进度" align="center">
              <template slot-scope="scope">
                <el-progress :percentage="parseInt(scope.row.verifyProgress)" color="#67C23A" />
              </template>
            </el-table-column>
            <el-table-column label="当前进展" align="center">
              <template slot-scope="scope">
                <el-tag :type="makeTagColorTypeByStatus(scope.row.status)">{{ scope.row.status | statusFilter }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
              <template slot-scope="{row,$index}">
                <el-button type="primary" size="mini" @click="handleUnitTaskVerify(props.row, row, $index)">
                  审核
                </el-button>
                <el-button size="mini" type="danger" @click="deleteUnitTask(row,$index)">
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column v-if="category == 1" width="50" align="center" label="序号">
        <template slot-scope="scope">
          <span>{{ scope.row.taskSerial }}</span>
        </template>
      </el-table-column>

      <el-table-column v-if="category < 5" width="100" align="center" label="任务类别">
        <template slot-scope="scope">
          <span>{{ categoryLabelTypes[scope.row.category][scope.row.labelType] }}</span>
        </template>
      </el-table-column>

      <el-table-column v-if="category == 4" width="110" align="center" label="任务标签">
        <template slot-scope="scope">
          <span>{{ scope.row.taskLabel }}</span>
        </template>
      </el-table-column>

      <el-table-column v-if="category == 5" width="110" align="center" label="事项编号">
        <template slot-scope="scope">
          <span>{{ scope.row.taskLabel }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="titleLabels[category]" :show-overflow-tooltip="true" width="220">
        <template slot-scope="scope">
          <span>{{ scope.row.title }}</span>
        </template>
      </el-table-column>

      <el-table-column align="center" :label="contentLabels[category]" :show-overflow-tooltip="true" width="280">
        <template slot-scope="scope">
          <span>{{ scope.row.content }}</span>
        </template>
      </el-table-column>

      <el-table-column label="责任部门" width="100" align="center">
        <template slot-scope="scope">{{ makeUnitName(scope.row.unitTasks) }}</template>
      </el-table-column>

      <el-table-column label="分管领导" width="100" align="center">
        <template slot-scope="scope">{{ makeParentUnitName(scope.row.unitTasks) }}</template>
      </el-table-column>
      <el-table-column label="报送进度" align="center">
        <template slot-scope="scope">
          <el-progress v-if="scope.row.unitTasks.length == 1" :percentage="parseInt(scope.row.unitTasks[0].reportProgress)" color="#409eff" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="完成进度" align="center">
        <template slot-scope="scope">
          <el-progress v-if="scope.row.unitTasks.length == 1" :percentage="parseInt(scope.row.unitTasks[0].verifyProgress)" color="#67C23A" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="当前进展" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.unitTasks.length == 1" :type="makeTagColorTypeByStatus(scope.row.unitTasks[0].status)">{{ makeUnitTaskStatus(scope.row.unitTasks) }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="230" class-name="small-padding fixed-width">
        <template slot-scope="{row,$index}">
          <el-button type="primary" size="mini" @click="handleVerify(row,$index)">
            审核
          </el-button>
          <el-button type="primary" size="mini" @click="handleUpdate(row)">
            编辑
          </el-button>
          <el-button size="mini" type="danger" @click="deleteData(row,$index)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form ref="updateTaskForm" v-loading="deleteUnitLoading" :rules="rules" :model="temp" label-position="left" label-width="auto" style="width: 400px; margin-left:50px;">
        <el-form-item label="任务类型">
          <div class="term">{{ categorys[temp.category] }}</div>
        </el-form-item>
        <el-form-item v-if="temp.category == 1" label="任务序号" prop="taskSerial">
          <el-input v-model="temp.taskSerial" placeholder="请输入任务序号" />
        </el-form-item>
        <el-form-item label="任务年份" prop="taskYear">
          <el-select v-model="temp.taskYear" class="filter-item" placeholder="请选择任务年份">
            <el-option v-for="item in years" :key="category + item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="category < 5" :label="typeLabels[category]" prop="labelType">
          <el-select v-model="temp.labelType" class="filter-item" placeholder="请选择任务类别">
            <el-option v-for="(item, index) in categoryLabelTypes[category]" :key="category + index" :label="item" :value="index" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="category == 4" label="任务标签" prop="taskLabel">
          <el-input v-model="temp.taskSerial" placeholder="请输入任务标签" />
        </el-form-item>
        <el-form-item v-if="category == 5" label="事项编号" prop="taskLabel">
          <el-input v-model="temp.taskSerial" placeholder="请输入事项编号" />
        </el-form-item>
        <el-form-item v-if="category != 7" :label="titleLabels[category]" prop="title">
          <el-input v-model="temp.title" placeholder="请输入任务事项标题" />
        </el-form-item>
        <el-form-item v-if="category == 7" :label="titleLabels[category]" prop="title">
          <el-select v-model="temp.title" default-first-option class="filter-item" placeholder="请选重点项目">
            <el-option v-for="(item, index) in videoTasks" :key="category + index" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item :label="contentLabels[category]" prop="content">
          <el-input v-model="temp.content" type="textarea" :rows="8" placeholder="请输入任务事项内容" />
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
          <el-select v-model="temp.unitIds" multiple filterable allow-create default-first-option class="filter-item" placeholder="请选择责任部门" @remove-tag="removeUnitTag">
            <el-option v-for="item in unitis" :key="category + item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="temp.category == 7" label="总投资额" prop="totalInvest">
          <el-input v-model="temp.totalInvest" placeholder="请输入总投资额" />
        </el-form-item>

        <el-form-item v-if="temp.category == 7" label="累计投资额" prop="cumulativeInvest">
          <el-input v-model="temp.cumulativeInvest" placeholder="请输入累计投资额" />
        </el-form-item>

        <el-form-item v-if="temp.category == 7" label="年度计划投资" prop="yearPlanInvest">
          <el-input v-model="temp.yearPlanInvest" placeholder="请输入年度计划投资" />
        </el-form-item>

        <el-form-item v-if="temp.category == 7" label="年度累计完成投资" prop="yearCumulativeDoneInvest">
          <el-input v-model="temp.yearCumulativeDoneInvest" placeholder="请输入年度累计完成投资" />
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
        <el-button type="primary" @click="dialogStatus==='create' ? createData() : updateData()">
          确定
        </el-button>
      </div>
    </el-dialog>

    <el-dialog title="审核操作" :visible.sync="dialogVerifyVisible">
      <el-form ref="dataForm" :model="temp" label-position="left" label-width="auto" style="width: 600px; margin-left:50px;">
        <el-form-item label="任务类型">
          <div class="term">{{ categorys[temp.category] }}</div>
        </el-form-item>
        <el-form-item v-if="temp.category == 1" label="任务序号">
          <div class="term">{{ temp.taskSerial }}</div>
        </el-form-item>
        <el-form-item label="任务年份">
          <div class="term">{{ temp.taskYear }}</div>
        </el-form-item>
        <el-form-item v-if="category < 5" :label="typeLabels[category]">
          <div class="term">{{ categoryLabelTypes[category][temp.labelType] }}</div>
        </el-form-item>
        <el-form-item v-if="category == 4" label="任务标签">
          <div class="term">{{ temp.taskLabel }}</div>
        </el-form-item>
        <el-form-item v-if="category == 5" label="事项编号">
          <div class="term">{{ temp.taskLabel }}</div>
        </el-form-item>
        <el-form-item :label="titleLabels[category]">
          <div class="term">{{ temp.title }}</div>
        </el-form-item>
        <el-form-item :label="contentLabels[category]">
          <div class="term">{{ temp.content }}</div>
        </el-form-item>
        <el-form-item label="调度时间">
          <div class="term">{{ makeReportDateTime(temp) }}</div>
        </el-form-item>
        <el-form-item v-if="temp.category == 7" label="总投资额">
          <div class="term">{{ temp.totalInvest }}</div>
        </el-form-item>

        <el-form-item v-if="temp.category == 7" label="累计投资额">
          <div class="term">{{ temp.cumulativeInvest }}</div>
        </el-form-item>

        <el-form-item v-if="temp.category == 7" label="年度计划投资">
          <div class="term">{{ temp.yearPlanInvest }}</div>
        </el-form-item>

        <el-form-item v-if="temp.category == 7" label="年度累计完成投资">
          <div class="term">{{ temp.yearCumulativeDoneInvest }}</div>
        </el-form-item>
        <el-form-item label="附件图片">
          <el-image v-for="(item, index) in fileList" :key="category + index" :src="item.url" fit="fit" :preview-src-list="taskImageUrls" style="width: 104px; height: 100px; padding-right: 5px; border-radius: 0px" />
        </el-form-item>
        <el-form-item v-if="temp.unitTasks" label="责任部门">
          <el-radio-group v-model="unitTask" @change="changeUnitTask">
            <el-radio-button v-for="item in temp.unitTasks" :key="category + item.id" :label="item">
              {{ item.unitName }}
            </el-radio-button>
          </el-radio-group>
        </el-form-item>
        <div v-loading="tracesLoading" style="margin: 10px;">
          <el-card v-for="item in traces" :key="category + item.id" class="box-card" style="margin-bottom: 10px;">
            <div slot="header" class="clearfix">
              <span>{{ item.content }}</span>
              <div v-if="item.attachments" style="margin-top: 10px; border: 2px">
                <el-image v-for="(image, index) in item.images" :key="category + index" :src="image.url" fit="fit" :preview-src-list="item.imageUrls" style="width: 84px; height: 80px; padding-right: 4px; border-radius: 0px; box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04)" />
              </div>
            </div>
            <template v-if="item.question">
              <div>
                <h4>存在问题：</h4>
                <span>{{ item.question }}</span>
              </div>
            </template>
            <template v-if="item.step">
              <div>
                <h4>推进措施：</h4>
                <span>{{ item.step }}</span>
              </div>
            </template>
            <template v-if="item.status >= 71 && item.status <= 73">
              <div style="padding-bottom: 15px;">
                <h4>审核进度：</h4>
                <el-progress :percentage="parseInt(item.progress)" />
              </div>
            </template>
            <template v-if="item.status == 31">
              <div style="padding-bottom: 15px;">
                <h4>报送进度：</h4>
                <el-progress :percentage="parseInt(item.progress)" />
              </div>
            </template>
            <div class="text Small" style="color: #a9a9a9; font: 13px">
              {{ '创建时间：' + item.createtime }}
            </div>
          </el-card>
        </div>
        <el-form-item label="审核操作">
          <el-radio-group v-model="verifyCategory" @change="changeVerifyStatus">
            <el-radio :label="1">督查催报</el-radio>
            <el-radio :label="2">退回重报</el-radio>
            <el-radio :label="3">进度审核</el-radio>
            <el-radio :label="4">呈报领导</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="verifyCategory === 1" label="催报描述" prop="content">
          <el-input v-model="trace.content" type="textarea" :rows="8" placeholder="请输入催报描述" />
        </el-form-item>
        <el-form-item v-if="verifyCategory === 2" label="退回原因" prop="content">
          <el-input v-model="trace.content" type="textarea" :rows="8" placeholder="请输入退回原因" />
        </el-form-item>
        <el-form-item v-if="verifyCategory === 3" label="审核进度" prop="progress">
          <el-input v-model="trace.progress" placeholder="请输入审核进度(百分比)" />
        </el-form-item>
        <el-form-item v-if="verifyCategory === 3" label="进展评价">
          <el-radio-group v-model="trace.status">
            <el-radio :label="71">序时推进</el-radio>
            <el-radio :label="72">进展较慢</el-radio>
            <el-radio :label="73">进展较快</el-radio>
            <el-radio :label="91">已完成</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="verifyCategory === 4" label="选择领导">
          <template>
            <el-checkbox-group v-model="reportLeaderUnits" @change="groupChange">
              <el-checkbox v-for="leaderUnit in leaderUnits" :key="leaderUnit.id" :label="leaderUnit">
                {{ leaderUnit.name }}
              </el-checkbox>
            </el-checkbox-group>
          </template>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVerifyVisible = false">
          取消
        </el-button>
        <el-button type="primary" @click="createTrace()">
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
import { fetchTaskList, fetchTaskListByFilter, fetchMonthContentTraceList, createTask, updateTask, deleteTask, deleteTasks } from '@/api/task'
import { fetchCommonUnits, fetchLeaderUnits } from '@/api/unit'
import { deleteUnitTask } from '@/api/unittask'
import { fetchTraces, newVerifyTrace, newReportTrace, newRushTrace, newBackTrace } from '@/api/trace'

export default {
  name: 'ReportTaskList',
  filters: {
    statusFilter(status) {
      const statusMap = {
        0: '未领取',
        1: '已领取',
        20: '上报领导',
        21: '领导批示',
        31: '已报送',
        50: '已逾期',
        51: '系统催报',
        52: '督查催报',
        71: '序时推进',
        72: '进度缓慢',
        73: '进度较快',
        74: '已退回',
        91: '完成'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      category: this.$route.path.split('=')[1],
      list: null,
      traces: null,
      listLoading: true,
      exportLoading: false,
      tracesLoading: true,
      deleteUnitLoading: false,
      unitis: null,
      fileList: [],
      taskImageUrls: [],
      leaderUnits: null,
      reportLeaderUnits: [],
      dialogImageUrl: '',
      dialogImageVisible: false,
      textMap: {
        update: '编辑更新',
        create: '发布任务'
      },
      listQuery: {
        status: null,
        leaderUnitId: null
      },
      attachmentUrl: 'http://api.superior.yooar.com/uploads/',
      verifyStatus: [
        { 'value': 0, 'label': '未领取' },
        { 'value': 1, 'label': '已领取' },
        { 'value': 20, 'label': '上报领导' },
        { 'value': 21, 'label': '领导批示' },
        { 'value': 31, 'label': '已报送' },
        { 'value': 50, 'label': '已逾期' },
        { 'value': 51, 'label': '系统催报' },
        { 'value': 52, 'label': '督查催报' },
        { 'value': 71, 'label': '序时推进' },
        { 'value': 72, 'label': '进度缓慢' },
        { 'value': 73, 'label': '进度较快' },
        { 'value': 74, 'label': '已退回' },
        { 'value': 91, 'label': '完成' }
      ],
      videoTasks: ['博兴县博瑞蛋白公司', '兴福孵化中心', '兴福批发市场', '中一厨具', '博兴县博昌仓储物流', '美厨厨业有限公司', '县第二污水处理厂', '县一中新校区', '市民文化中心', '博兴县锦秋棚户区改造项目'],
      trace: { 'status': 0, 'content': '', 'userId': 0, 'progress': '' },
      statusContents: { '71': '当前工作进度序时推进', '72': '当前工作进度缓慢', '73': '当前工作进展较快', '91': '当前工作已调度完成' },
      verifyCategory: 0,
      unitTask: null,
      type: 'expand',
      years: [new Date().getFullYear(), new Date().getFullYear() + 1],
      temp: {
        id: undefined,
        title: '',
        content: ''
      },
      selectTasks: [],
      dialogStatus: '',
      dialogFormVisible: false,
      dialogVerifyVisible: false,
      typeLabels: ['', '任务类别', '任务排名', '任务类别', '任务类别', '任务排名', '任务类别', '任务类别', '任务排名', '任务类别'],
      titleLabels: ['', '重点工作', '具体项目', '建议(提案)名称', '议定事项', '交办事项', '督查事项', '项目名称', '线索标题', '民生实事'],
      contentLabels: ['', '推进计划', '办理细则', '办理要求', '工作要求', '办理要求', '具体要求', '项目简介', '具体描述', '具体要求'],
      categorys: ['', '政府工作报告', '7+3重点改革任务', '建议提案', '会议议定事项', '领导批示', '专项督查', '重点项目', '线索督查', '民生在线', '企业诉求'],
      categoryLabelTypes: {
        1: ['预期目标类', '试点验收类', '定量类', '定性类'],
        2: ['第一名', '第二名', '第三名', '第四名', '第五名', '第六名', '第七名', '第八名', '第九名', '第十名', '未排名'],
        3: ['人大', '政协'],
        4: ['全体会议', '常务会议', '县长办公室会议', '专题会议'],
        5: [],
        6: [],
        7: []
      },
      rules: {
        taskSerial: [{ required: true, message: '请填写任务序号', trigger: 'blur' }],
        taskLabel: [{ required: true, message: '请填写任务标签', trigger: 'blur' }],
        taskYear: [{ required: true, message: '请选择任务年度', trigger: 'blur' }],
        labelType: [{ required: true, message: '请选择任务类型', trigger: 'blur' }],
        reportCycle: [{ required: true, message: '请选择调度周期', trigger: 'blur' }],
        reportDate: [{ required: true, message: '请填写报送时间', trigger: 'blur' }],
        unitIds: [{ required: true, message: '请选择责任部门', trigger: 'blur' }],
        title: [{ required: true, message: '请填写标题', trigger: 'blur' }],
        content: [{ required: true, message: '请填写消息内容', trigger: 'blur' }]
      }
    }
  },
  created() {
    // console.log(this.category)
    // console.log(this.$route.path)
    this.getCommonUnits()
  },
  methods: {
    getList() {
      this.listLoading = true
      fetchTaskList(this.category).then(response => {
        this.list = response.data
        this.listLoading = false
      })
    },
    makeTagColorTypeByStatus(status) {
      if (status === '0') {
        return 'success'
      }
      if (status === '1') {
        return 'info'
      }
      if (status === '31') {
        return ''
      }
      return 'danger'
    },
    groupChange(value) {
      console.log(value)
      console.log(this.reportLeaderUnits)
    },
    handleFilter() {
      this.listLoading = true
      const tempData = Object.assign({}, this.listQuery)
      tempData.category = this.category
      fetchTaskListByFilter(tempData).then(response => {
        this.list = response.data
        this.listLoading = false
      })
    },
    getRowClassName(row, index) {
      if (row && row.row.unitTasks.length === 1) {
        return 'row-expand-cover'
      }
    },
    makeRowExpandType(row) {
      return '-'
    },
    makeUnitName(unitTasks) {
      if (unitTasks && unitTasks.length === 1) {
        return unitTasks[0].unitName
      }
      return '-'
    },
    makeParentUnitName(unitTasks) {
      if (unitTasks && unitTasks.length === 1) {
        return unitTasks[0].unitParentName
      }
      return '-'
    },
    makeTaskProgress(unitTasks) {
      if (unitTasks && unitTasks.length === 1) {
        return unitTasks[0].verifyProgress
      }
      return '-'
    },
    makeUnitTaskStatus(unitTasks) {
      if (unitTasks && unitTasks.length === 1) {
        return this.$options.filters['statusFilter'](unitTasks[0].status)
      }
      return '-'
    },
    makeReportDateTime(task) {
      if (task.reportCycle === '1') {
        return '每月' + task.reportDate + '日'
      }
      return task.reportDate
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
    getCommonUnits() {
      this.listLoading = true
      fetchCommonUnits().then(response => {
        this.unitis = response.data
        this.getLeaderUnits()
      })
    },
    getLeaderUnits() {
      this.listLoading = true
      fetchLeaderUnits().then(response => {
        this.leaderUnits = response.data
        console.log(this.leaderUnits)
        this.getList()
      })
    },
    removeUnitTag(unitId) {
      this.deleteUnitLoading = true
      deleteUnitTask(this.temp.id, unitId).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Update Successfully',
          type: 'success',
          duration: 2000
        })
        this.deleteUnitLoading = false
      })
    },
    handleCreate() {
      // this.resetTemp()
      this.dialogStatus = 'create'
      this.dialogFormVisible = true
      this.temp = { 'category': this.category }
      this.$nextTick(() => {
        this.$refs['updateTaskForm'].clearValidate()
      })
    },
    handleExport() {
      this.exportLoading = true
      fetchMonthContentTraceList(this.category).then(response => {
        var contentTraces = response.data
        // console.log(contentTraces)
        var tempList = []
        this.list.forEach(element => {
          element.unitTasks.forEach(elementUnitTask => {
            elementUnitTask.taskSerial = element.taskSerial
            elementUnitTask.taskLabel = element.taskLabel
            elementUnitTask.title = element.title
            elementUnitTask.content = element.content
            if (Object.keys(contentTraces).indexOf(elementUnitTask['id']) >= 0) {
              var trace = contentTraces[elementUnitTask['id']]
              elementUnitTask.monthContent = trace.content
            } else {
              elementUnitTask.monthContent = ''
            }
            elementUnitTask.dutyer = '责任人：' + elementUnitTask.handleUserName + '分管责任人：' + elementUnitTask.partReponsibilityUserName + '主要责任人：' + elementUnitTask.responsibilityUserName
            tempList.unshift(elementUnitTask)
          })
        })
        this.exportExcel(tempList)
      })
    },
    exportExcel(unitTasks) {
      import('@/vendor/Export2Excel').then(excel => {
        // 序号	重点工作	推进计划	进展情况	完成进度	责任人	责任单位	备注
        const tHeader = ['任务序号', '任务类别', '重点工作', '推进计划', '进展情况', '完成进度', '责任人', '责任单位', '进展亮灯']
        const filterVal = ['taskSerial', 'taskLabel', 'title', 'content', 'monthContent', 'verifyProgress', 'dutyer', 'unitName', 'verifyProgress']
        const data = this.formatJson(filterVal, unitTasks)
        excel.export_json_to_excel({
          header: tHeader,
          data,
          autoWidth: false,
          filename: '政府工作报告(' + this.getNowDate() + ')'
        })
        this.exportLoading = false
      })
    },
    getNowDate() {
      var date = new Date()
      var y = date.getFullYear()
      var m = date.getMonth() + 1
      var d = date.getDate()
      var H = date.getHours()
      var mm = date.getMinutes()
      var ss = date.getSeconds()
      m = m < 10 ? '0' + m : m
      d = d < 10 ? '0' + d : d
      H = H < 10 ? '0' + H : H
      return y + '-' + m + '-' + d + ' ' + H + '-' + mm + '-' + ss
    },
    formatJson(filterVal, jsonData) {
      return jsonData.map(v => filterVal.map(j => { return v[j] }))
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
    createData() {
      this.$refs['updateTaskForm'].validate((valid) => {
        if (valid) {
          this.temp.category = this.category
          this.temp.attachmentids = this.makeAttachmentIds()
          createTask(this.temp).then((response) => {
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
        this.$refs['updateTaskForm'].clearValidate()
      })
    },
    updateData() {
      this.$refs['updateTaskForm'].validate((valid) => {
        if (valid) {
          var tempData = Object.assign({}, this.temp)
          tempData.attachmentids = this.makeAttachmentIds()
          updateTask(tempData).then((response) => {
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
    handleVerify(row, index) {
      this.temp = Object.assign({}, row) // copy obj
      var urls = []
      var files = []
      if (this.temp.attachmentids.length > 0) {
        this.temp.attachmentids.split(',').forEach(element => {
          urls.push(this.attachmentUrl + element)
          files.push({ 'name': element, 'url': this.attachmentUrl + element })
        })
      }
      this.fileList = files
      this.taskImageUrls = urls
      this.unitTask = this.temp.unitTasks[0]
      this.dialogStatus = 'update'
      this.dialogVerifyVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
      this.getTraces(this.temp.id, this.unitTask.unitId)
    },
    deleteData(row, index) {
      deleteTask(row.id).then(() => {
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
    handleUnitTaskVerify(task, unitTask, index) {
      this.temp = Object.assign({}, task) // copy obj
      this.unitTask = this.temp.unitTasks[index]
      this.dialogStatus = 'update'
      this.dialogVerifyVisible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].clearValidate()
      })
      this.getTraces(this.temp.id, this.unitTask.unitId)
    },
    deleteUnitTask(row, index) {
      this.listLoading = true
      deleteUnitTask(row.taskId, row.unitId).then(() => {
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
        this.listLoading = false
      })
    },
    changeUnitTask(unitTask) {
      this.trace = {}
      this.verifyCategory = 0
      this.getTraces(this.temp.id, unitTask.unitId)
    },
    getTraces(taskId, unitId) {
      this.tracesLoading = true
      fetchTraces(taskId, unitId).then(response => {
        this.traces = response.data
        this.tracesLoading = false
      })
    },
    changeVerifyStatus(verifyCategory) {
      this.trace = { 'status': 0, 'content': '', 'userId': 0, 'progress': '' }
      this.reportLeaderUnits = []
      if (verifyCategory === 1) {
        this.trace.status = 52
      } else if (verifyCategory === 2) {
        this.trace.status = 74
      } else if (verifyCategory === 4) {
        this.trace.status = 20
      } else {
        this.trace.status = 0
      }
    },
    handleSelectionChange(rows) {
      this.selectTasks = rows
      console.log(rows)
    },
    handleDeleteSelect() {
      this.listLoading = true
      var taskIds = this.selectTasks.map(element => {
        return element.id
      })
      console.log(taskIds)
      deleteTasks({ 'ids': taskIds }).then(response => {
        this.$notify({
          title: 'Success',
          message: 'Delete Successfully',
          type: 'success',
          duration: 2000
        })
        this.getList()
      })
    },
    createTrace() {
      if (this.verifyCategory === 0) {
        this.$notify({
          title: 'Error',
          message: '请选择审核操作',
          type: 'error',
          duration: 2000
        })
        return
      }
      this.trace.unitTaskId = this.unitTask.id
      this.trace.unitId = 0
      this.trace.userId = 0
      if (!this.trace.progress) {
        this.trace.progress = 0
      }
      this.tracesLoading = true
      // 督查催报
      if (this.verifyCategory === 1) {
        this.trace.status = 52
        newRushTrace(this.trace).then(response => {
          this.tracesLoading = false
          this.traces.unshift(response.data)
          this.$notify({
            title: 'Success',
            message: '审核成功',
            type: 'success',
            duration: 2000
          })
          this.trace = {}
          this.dialogVerifyVisible = false
        })
      }
      // 督查退回
      if (this.verifyCategory === 2) {
        this.trace.status = 74
        newBackTrace(this.trace).then(response => {
          this.tracesLoading = false
          this.traces.unshift(response.data)
          this.$notify({
            title: 'Success',
            message: '审核成功',
            type: 'success',
            duration: 2000
          })
          this.trace = {}
          this.dialogVerifyVisible = false
        })
      }
      // 督查审核
      if (this.verifyCategory === 3) {
        if ((this.trace.status >= 71 && this.trace.status <= 73) || this.trace.status === 91) {
          this.trace.content = this.statusContents[this.trace.status]
        }
        console.log(this.trace)
        newVerifyTrace(this.trace).then(response => {
          this.tracesLoading = false
          this.traces.unshift(response.data)
          this.$notify({
            title: 'Success',
            message: '审核成功',
            type: 'success',
            duration: 2000
          })
          this.trace = {}
          this.dialogVerifyVisible = false
        })
      }
      // 上报领导
      if (this.verifyCategory === 4) {
        if (this.reportLeaderUnits.length === 0) {
          this.$notify({
            title: 'Error',
            message: '请选择要呈报的领导',
            type: 'error',
            duration: 2000
          })
          return
        }
        this.trace.status = 20
        this.trace.content = ''
        this.trace.leaderUnitIds = ''
        this.reportLeaderUnits.forEach(element => {
          this.trace.leaderUnitIds += this.trace.leaderUnitIds.length > 0 ? ',' : ''
          this.trace.leaderUnitIds += element.id
          this.trace.content += this.trace.content.length > 0 ? ',' : ''
          this.trace.content += element.name
        })
        this.trace.content = '已上报给【' + this.trace.content + '】领导'
        newReportTrace(this.trace).then(response => {
          this.tracesLoading = false
          this.traces.unshift(response.data)
          this.$notify({
            title: 'Success',
            message: '审核成功',
            type: 'success',
            duration: 2000
          })
          this.trace = {}
          this.dialogVerifyVisible = false
        })
      }
    }
  }
}
</script>

<style>
.row-expand-cover .el-table__expand-icon{visibility: hidden;}
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
    width: 68px;
    height: 68px;
    line-height: 68px;
    text-align: center;
  }
  .avatar {
    width: 68px;
    height: 68px;
    display: block;
  }
</style>
