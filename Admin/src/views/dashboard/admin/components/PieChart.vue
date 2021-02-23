<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import echarts from 'echarts'
require('echarts/theme/macarons') // echarts theme
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '350px'
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c}件 ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: ['政府工作报告', '建议提案', '7+3重点改革任务', '会议议定事项', '领导批示', '专项督查', '重点项目']
        },
        series: [
          {
            name: '调度事项',
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '38%'],
            data: [
              { value: 320, name: '政府工作报告' },
              { value: 240, name: '建议提案' },
              { value: 149, name: '7+3重点改革任务' },
              { value: 100, name: '会议议定事项' },
              { value: 59, name: '领导批示' },
              { value: 55, name: '专项督查' },
              { value: 35, name: '重点项目' }
            ],
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      })
    }
  }
}
</script>
