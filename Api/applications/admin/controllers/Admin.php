<?php
class Admin extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function index()
    {/*{{{*/
        if ($this->is_login())
        {
            $data['activeonelevel'] = "task";
            $data['activetwolevel'] = "task";
            $this->load->view('admin', $data);
        }

    }/*}}}*/

    public function report($category = 0, $placeholder = 0)
    {/*{{{*/
        $categorys  = array("", 
                    "政府工作报告",
                    "市委市政府重大决策部署",
                    "建议提案",
                    "会议议定事项", 
                    "领导批示",
                    "专项督查",
                    "重点项目"
                );
        if ($this->is_login())
        {
            $exDate = $this->input->post('exDate');
            $exDate = !isset($exDate) ? date('Y-m') : $exDate; 
            $data['activeonelevel'] = "report";
            $data['activetwolevel'] = $category;
            $data['activethreelevel'] = 0;
            $data['category'] = $category;
            $data['exDate'] = $exDate;
            $data['title'] = $categorys[$category];
            if ($category > 0 && $category < 8 && $category != 4)
            {
				//按月份查询
				if ($category == 1 || $category == 3 || $category == 5)
				{
					$data['tasks'] = $this->getTaskReport($category, $exDate);
					$this->load->view('report', $data);
				}

				//起止时间查询
				if ($category == 2 || $category == 6 || $category == 7)
				{
					$startDate = $this->input->post('startDate');
					$endDate = $this->input->post('endDate');
					$startDate = !isset($startDate) ? date('Y-m').'-01' : $startDate; 
					$endDate = !isset($endDate) ? date('Y-m-d') : $endDate; 

					$data['startDate'] = $startDate;
					$data['endDate'] = $endDate;
					$data['tasks'] = $this->getTaskReportBetweenDays($category, $startDate, $endDate);
					$this->load->view('reportbydate', $data);
				}

				//
				if ($category == 4)
				{
					$data['tasks'] = array();
					$this->load->view('report', $data);
				}
            }
        }
    }/*}}}*/

    public function reportmeeting($category = 0, $childType = 0)
    {/*{{{*/
        $categorys  = array("", 
                    "政府工作报告",
                    "市委市政府重大决策部署",
                    "建议提案",
                    "会议议定事项", 
                    "领导批示",
                    "专项督查",
                    "重点项目"
                );
        $childTypes = array("", 
                    "全体会议",
                    "常务会议",
                    "县长办公室会议",
                    "专题会议"
                );
        if ($this->is_login())
        {
            $taskLabel = $this->input->post('taskLabel');
            $data['activeonelevel'] = "report";
            $data['activetwolevel'] = $category;
            $data['activethreelevel'] = $childType;
            $data['childType'] = $childType;
            $data['category'] = $category;
            $data['curTaskLabel'] = $this->input->post('taskLabel');
            $data['title'] = $categorys[$category]."__".$childTypes[$childType];
            $data['task'] = array();
            if ($childType > 0 && $childType < 5)
            {
                $taskLabels = array();
                $sql = "select taskLabel from tbl_task where childType = ".$childType." and category = 4 group by taskLabel;";
                $taskLabels = $this->admin_model->query($sql);
                $taskLabels = json_decode(json_encode($taskLabels), true);
                $data['taskLabels'] = $taskLabels;
            }

            if ($category == 4 && isset($taskLabel) && strlen($taskLabel))
            {
                $data['tasks'] = $this->getMeetingReport($category, $childType, $taskLabel);
            }
            $this->load->view('reportmeeting', $data);
        }
    }/*}}}*/

    public function exportEx($category = 0, $childType = 0)
    {/*{{{*/
        $exDate = $this->input->post('exDate');
        $taskLabel = $this->input->post('taskLabel');
        if ($category <= 0 || $category > 7)
        {
            echo '<script>alert("任务类型错误，导出失败");</script>';
            return;
        }
        if ($category == 4)
        {
            if ($childType <= 0 || $childType >= 5)
            {
                echo '<script>alert("任务种类错误，导出失败");</script>';
                return;

            }
            if (!isset($taskLabel) || !strlen($taskLabel))
            {
                echo '<script>alert("任务标签错误，导出失败");</script>';
                return;
            }
        }
        else if ($category == 1 || $category == 3 || $category == 5)
        {
            if (!isset($exDate)) 
            {
                echo '<script>alert("选择查看历史记录日期错误，导出失败");</script>';
                return;
            }

        }
		$this->load->library('PHPExcel');
        $this->load->library('PHPExcel/IOFactory');
        $objPHPExcel = new PHPExcel();

        //设置proerties;
        $objPHPExcel->getProperties()
            ->setCreator("WangHua")
            ->setLastModifiedBy("WangHua")
            ->setTitle("Office 2007 XLSX Test Document")
            ->setSubject("Office 2007 XLSX Test Document")
            ->setDescription("Test document for Office 2007 XLSX, generated using PHP classes.")
            ->setKeywords("office 2007 openxml php")
            ->setCategory("Test result file");

        //设置locale
        $locale = 'pt_br';
        $validLocale = PHPExcel_Settings::setLocale($locale);
        if (!$validLocale) {
                echo 'Unable to set locale to '.$locale." - reverting to en_us<br />\n";
        }
        //设置默认字体
        $objPHPExcel->getDefaultStyle()->getFont()->setName('宋体');

        $objPHPExcel->getActiveSheet()->getColumnDimension('A')->setWidth(10);
        $objPHPExcel->getActiveSheet()->getColumnDimension('B')->setWidth(25);
        $objPHPExcel->getActiveSheet()->getColumnDimension('C')->setWidth(50);
        $objPHPExcel->getActiveSheet()->getColumnDimension('D')->setWidth(80);
        $objPHPExcel->getActiveSheet()->getColumnDimension('E')->setWidth(20);
        $objPHPExcel->getActiveSheet()->getColumnDimension('F')->setWidth(20);
        $objPHPExcel->getActiveSheet()->getColumnDimension('G')->setWidth(20);
        $objPHPExcel->getActiveSheet()->getColumnDimension('H')->setWidth(25);

        //cell style
        $objPHPExcel->getActiveSheet()->getStyle('A')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER)
            ->setWrapText(true);
        $objPHPExcel->getActiveSheet()->getStyle('B')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setWrapText(true);
        $objPHPExcel->getActiveSheet()->getStyle('C')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setWrapText(true);
        $objPHPExcel->getActiveSheet()->getStyle('D')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setWrapText(true);
        $objPHPExcel->getActiveSheet()->getStyle('E')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setWrapText(true);
        $objPHPExcel->getActiveSheet()->getStyle('F')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setWrapText(true);
        $objPHPExcel->getActiveSheet()->getStyle('G')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setWrapText(true);
        if ($category == 1) {
            $objPHPExcel->getActiveSheet()->getStyle('H')->getAlignment()
            ->setVertical(PHPExcel_Style_Alignment::VERTICAL_CENTER)
            ->setWrapText(true);
        }

        // Set cell with a string value
        $objPHPExcel->getActiveSheet()->setCellValue('A1', '序号');
        $objPHPExcel->getActiveSheet()->setCellValue('B1', '重点工作');
        $objPHPExcel->getActiveSheet()->setCellValue('C1', '年度目标任务');
        $objPHPExcel->getActiveSheet()->setCellValue('D1', '进展情况');
        $objPHPExcel->getActiveSheet()->setCellValue('E1', '责任人');
        $objPHPExcel->getActiveSheet()->setCellValue('F1', '责任部门');
        $objPHPExcel->getActiveSheet()->setCellValue('G1', '备注');
        if ($category == 1) $objPHPExcel->getActiveSheet()->setCellValue('H1', '推进计划');

        $objPHPExcel->getActiveSheet()->getStyle('A1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
        $objPHPExcel->getActiveSheet()->getStyle('A1')->getFont()->setSize(16)->setBold(true);
        $objPHPExcel->getActiveSheet()->getStyle('A1')->getAlignment()
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);

        $objPHPExcel->getActiveSheet()->getStyle('B1')->getAlignment()
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);
        $objPHPExcel->getActiveSheet()->getStyle('B1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
        $objPHPExcel->getActiveSheet()->getStyle('B1')->getFont()->setSize(16)->setBold(true);

        $objPHPExcel->getActiveSheet()->getStyle('C1')->getAlignment()
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);
        $objPHPExcel->getActiveSheet()->getStyle('C1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
        $objPHPExcel->getActiveSheet()->getStyle('C1')->getFont()->setSize(16)->setBold(true);

        $objPHPExcel->getActiveSheet()->getStyle('D1')->getAlignment()
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);
        $objPHPExcel->getActiveSheet()->getStyle('D1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
        $objPHPExcel->getActiveSheet()->getStyle('D1')->getFont()->setSize(16)->setBold(true);

        $objPHPExcel->getActiveSheet()->getStyle('E1')->getAlignment()
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);
        $objPHPExcel->getActiveSheet()->getStyle('E1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
        $objPHPExcel->getActiveSheet()->getStyle('E1')->getFont()->setSize(16)->setBold(true);

        $objPHPExcel->getActiveSheet()->getStyle('F1')->getAlignment()
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);
        $objPHPExcel->getActiveSheet()->getStyle('F1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
        $objPHPExcel->getActiveSheet()->getStyle('F1')->getFont()->setSize(16)->setBold(true);


        $objPHPExcel->getActiveSheet()->getStyle('G1')->getAlignment()
            ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);
        $objPHPExcel->getActiveSheet()->getStyle('G1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
        $objPHPExcel->getActiveSheet()->getStyle('G1')->getFont()->setSize(16)->setBold(true);

        if ($category == 1) {
            $objPHPExcel->getActiveSheet()->getStyle('H1')->getAlignment()
                ->setHorizontal(PHPExcel_Style_Alignment::HORIZONTAL_CENTER);
            $objPHPExcel->getActiveSheet()->getStyle('H1')->getFont()->getColor()->setARGB(PHPExcel_Style_Color::COLOR_BLACK);
            $objPHPExcel->getActiveSheet()->getStyle('H1')->getFont()->setSize(16)->setBold(true);
        }

        if ($category == 4)
        {
            $data = $this->getMeetingReport($category, $childType, $taskLabel);
        }
		else if ($category == 2 || $category == 6 || $category == 7)
		{
			$startDate = $this->input->post('startDate');
			$endDate = $this->input->post('endDate');
            if (!isset($startDate) || !isset($endDate)) 
            {
                echo '<script>alert("请选择查看录日期，导出失败");</script>';
                return;
            }
			else 
			{
				$data = $this->getTaskReportBetweenDays($category, $startDate, $endDate);
			}
		}
        else
        {
            $data = $this->getTaskReport($category, $exDate);
        }

        $i = 2;
        $progrezs = array('', '任务完成', '进展较快', '序时推进', '进度缓慢');
        foreach($data as $task)
        { 
            $objPHPExcel->getActiveSheet()->setCellValue('A' . $i, ($i - 1).''); 
            $objPHPExcel->getActiveSheet()->setCellValue('B' . $i, $task['name']); 
            $objPHPExcel->getActiveSheet()->setCellValue('C' . $i, $task['plan']); 
            $objPHPExcel->getActiveSheet()->setCellValue('D' . $i, $task['content']); 
            $objPHPExcel->getActiveSheet()->setCellValue('E' . $i, $task['duumvir']); 
            $objPHPExcel->getActiveSheet()->setCellValue('F' . $i, $task['unitName']); 
            $objPHPExcel->getActiveSheet()->setCellValue('G' . $i, $progrezs[$task['progress']]); 
            if ($category == 1) $objPHPExcel->getActiveSheet()->setCellValue('H' . $i, $task['planDetail']); 
            $i ++; 
        }

        $objPHPExcel->getActiveSheet()->setTitle('政府工作报告');
        $objPHPExcel->setActiveSheetIndex(0);

        ob_end_clean();//清除缓冲区,避免乱码
        header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        header('Content-Disposition: attachment;filename="政府工作报告.xls"');
        header('Cache-Control: max-age=0');

        $objWriter = IOFactory::createWriter($objPHPExcel, 'Excel5');
        //$objWriter = IOFactory::createWriter($objPHPExcel, 'Excel2007');
        $objWriter->save('php://output'); 

    }/*}}}*/

    public function importEx()
    {/*{{{*/
		$this->load->library('PHPExcel');
        $this->load->library('PHPExcel/IOFactory');

        $filePath = 'uploads/政府工作报告6月份进展情况.xls';
        $PHPExcel = new PHPExcel();
         
        /**默认用excel2007读取excel，若格式不对，则用之前的版本进行读取*/
        $PHPReader = new PHPExcel_Reader_Excel2007();
        if(!$PHPReader->canRead($filePath)){
            $PHPReader = new PHPExcel_Reader_Excel5();
            if(!$PHPReader->canRead($filePath)){
                echo 'no Excel';
                return ;
            }
        }

        $columns = array(1 => 'A', 2 => 'B', 3 => 'C', 4 => 'D', 5 => 'E', 6 => 'F', 7 => 'G', 8 => 'H', 9 => 'I', 10 => 'J', 11 => 'K', 12 => 'L', 13 => 'M', 14 => 'N', 15 => 'O', 16 => 'P', 17 => 'Q', 18 => 'R', 19 => 'S', 20 => 'T', 21 => 'U', 22 => 'V', 23 => 'W', 24 => 'X', 25 => 'Y', 26 => 'Z');

        $PHPExcel = $PHPReader->load($filePath);
        /**读取excel文件中的第一个工作表*/
        $currentSheet = $PHPExcel->getSheet(6);
        /**取得一共有多少行*/
        $allRow = $currentSheet->getHighestRow();
        $res = array();
        for($currentRow = 3; $currentRow <= $allRow; $currentRow++){
            for ($column = 1; $columns[$column] != 'F'; $column++) {
                $val = $currentSheet->getCellByColumnAndRow($column, $currentRow)->getCalculatedValue();
                if ($val == null)
                {
                    $val = $res[$currentRow - 4][$column];
                }
                $res[$currentRow-3][$column] = $val;
            }
       }
        //echo json_encode($res, true);
        return $res;
    }/*}}}*/

    public function autoNewReport()
    {/*{{{*/
        $this->load->model('unit_model');
        $this->load->model('task_model');
        $units = $this->unit_model->get_where(array('role' => 4));
        $tasks = $this->importEx();
        foreach ($tasks as $task)
        {
            foreach($units as $unit)
            {
                //判断某字符串中是否包含某字符串的方法
                if(strpos($task[5], $unit->name) !== false)
                {
                    $data = array(
                        'category' => 1,
                        'unitid' => $unit->id,
                        'name' => $task[1],
                        'plan' => $task[2],
                        'reportDate' => '3',
                        'reportType' => 1,
                        'priority' => 2,
                    );
                    $id = $this->task_model->create_id($data);
                }
            }
        }
    }/*}}}*/

    public function getTaskReportBetweenDays($category, $startDate = '', $endDate = '')
    {/*{{{*/
        $sql = 'select * from ';
        $sql .= '(select a.id, a.name, a.plan , a.planDetail, u.name as unitName, a.progress, a.updatetime, ';
        $sql .= '(select content from tbl_trace where taskId = a.id and type = 3) as duumvir, ';
        $sql .= "(select group_concat(content, char(10)) from tbl_trace where taskId = a.id  and type = 0 and date_format(createTime, '%Y-%m-%d') >= date_format('".$startDate."', '%Y-%m-%d') and date_format(createTime, '%Y-%m-%d') <= date_format('".$endDate."', '%Y-%m-%d')) as content ";
        $sql .= 'from tbl_task a, tbl_unit u where a.valid = 1 and a.unitId = u.id and category = '.$category.')';
        $sql .= " as tt where tt.content != '' or date_format(updatetime, '%Y-%m-%d') >= date_format('".$startDate."', '%Y-%m-%d') and date_format(updatetime, '%Y-%m-%d') <= date_format('".$endDate."', '%Y-%m-%d') order by id;";
        $result = $this->admin_model->query($sql);
        $result = json_decode(json_encode($result), true);
        return $result;
    }/*}}}*/

    public function getTaskReport($category, $exDate = '')
    {/*{{{*/
        if (!isset($exDate) || !strlen($exDate)) $exDate = date('Y-m-d');
        if (strlen($exDate) == 7) $exDate.= '-01';
        $sql = 'select * from ';
        $sql .= '(select a.id, a.name, a.plan , a.planDetail, u.name as unitName, a.progress, a.updatetime, ';
        $sql .= '(select content from tbl_trace where taskId = a.id and type = 3 limit 1) as duumvir, ';
        $sql .= "(select group_concat(content, char(10)) from tbl_trace where taskId = a.id  and type = 0 and month(createTime) = month('".$exDate."')  and year(createTime) = year('".$exDate."')) as content ";
        $sql .= 'from tbl_task a, tbl_unit u where a.valid = 1 and a.unitId = u.id and category = '.$category.')';
        $sql .= " as tt where tt.content != '' or date_format(tt.updatetime, '%Y-%m') = date_format('".$exDate."','%Y-%m') order by id;";
        $result = $this->admin_model->query($sql);
        $result = json_decode(json_encode($result), true);
        return $result;
    }/*}}}*/

    public function getMeetingReport($category, $childType = 0, $taskLabel  = '')
    {/*{{{*/
        $sql = "select t.name, t.plan, t.taskLabel, u.name as unitName, t.progress, t.updatetime, (select content from tbl_trace where taskId = t.id and type = 3 limit 1) as duumvir, (select group_concat(content, char(10)) from tbl_trace where taskId = t.id  and type = 0) as content from tbl_task t, tbl_unit u where t.valid = 1 and t.unitId = u.id and t.childType = ".$childType." and t.category = 4 and t.taskLabel = '".$taskLabel."';";
        $result = $this->admin_model->query($sql);
        $result = json_decode(json_encode($result), true);
        return $result;
    }/*}}}*/

    //自动化写数据
    public function autoNew()
    {/*{{{*/
        $this->load->model('unit_model');
        $sql = 'select id from tbl_unit where role = 4 and id > 33';
        $units = $this->unit_model->get_where(array('role' => 4, 'id > ' => 33));
        $this->load->model('task_model');
        foreach ($units as $unit)
        {
            $sql = "INSERT INTO `tbl_task` (`category`, `unitid`, `name`, `plan`, `reportDate`, `reportType`, `priority`) VALUES ('4', ".$unit->id.", '上半年全市科学发展观摩准备工作', '全市上半年科学发展观摩将于5月份左右组织举办，县政府各部门、单位要坚决杜绝“临时抱佛脚”的想法，提前谋划，结合单位工作情况，筛选工作亮点，提出初步建议意见，把观摩作为集中展现我县发展成就、塑造良好对外形象的重要窗口，全力做好全市科学发展观摩筹备工作。', '3', '1', '2');";
            $this->task_model->excute($sql);
        }

    }/*}}}*/

    public function newtask()
    {/*{{{*/
        if ($this->is_login())
        {

            $config['upload_path']='./uploads/';
            $config['allowed_types']='gif|jpg|png';
            $config['overwrite'] = false;
            $config['encrypt_name'] = true;
            $config['remove_spaces'] = true;
            $this->load->library('upload', $config);
            $attachments = array();
            foreach ($_FILES as $key => $value) {
                if (!empty($key) && !empty($value['name'])) {
                    if ($this->upload->do_upload($key)) {
                        //上传成功
                        $attachments[] = $this->upload->data()['file_name'];
                    } else {
                        //上传失败
                        echo '<script>alert("图片上传失败,请重新上传!"); window.history.back();</script>';
                        return;

                    }
                }
            }
            
            $data = array(
                'category' => $this->input->post('category'),
                'name' => $this->input->post('taskname'),
                'plan' => $this->input->post('plan'),
                'reportDate' => $this->input->post('reportDate'),
                'reportType' => $this->input->post('reportType'),
                'priority' => $this->input->post('priority'),
				'createtime' => date("Y-m-d h:i:s"),
                'groupTask' => md5(uniqid()),
            );
            if ($data['reportType'] == 1 || $data['reportType'] == 3)
            {
                $data['reportTime'] = $this->input->post('reportTime');
            }

            if ($data['category'] == 1) 
            {
                $data['planDetail'] = $this->input->post('planDetail');
            }
            if ($data['category'] == 2) 
            {
                $data['sequence'] = $this->input->post('sequence');
            }
            if ($data['category'] == 3) 
            {
                $data['childType'] = $this->input->post('childType');
            }
            //1:全体会议,2:常务会议，3:县长办公室会议，4:专题会议
            if ($data['category'] == 4)
            {
                $data['childType'] = $this->input->post('childType');
                $data['taskLabel'] = $this->input->post('taskLabel');
            }
            if (!in_array(null, array_values($data)))
            {
                //有附件
                if (count($attachments)) 
                {
                    $data['attachmentids'] = implode(',', $attachments);
                }
                $this->load->model('task_model', '', true);

                $unitIds = $this->input->post('unitIds');
                if (count($unitIds)) 
                {
                    require_once 'Jpush.php';
                    $jpush = Jpush::getInstance();
                    foreach($unitIds as $unitId)
                    {
                        $data['unitId'] = $unitId;
                        $id = $this->task_model->create_id($data);

                        $jpush->pushTaskTip($data['name'], array('id' => $id, 'type' => 2), ''.$unitId);
                    }
                }
                echo '<script>alert("发布任务成功!"); window.location.href="newtask"</script>';
            }
            else
            {
                $data['activeonelevel'] = "task";
                $data['activetwolevel'] = "task";
                $data['category'] = array(
                    "1"=>"政府工作报告",
                    "2"=>"市委市政府重大决策部署",
                    "3"=>"建议提案",
                    "4"=>"会议议定事项", 
                    "5"=>"领导批示",
                    "6"=>"专项督查",
                    "7"=>"重点项目"
                );
                $this->load->model('unit_model');
                $units = $this->unit_model->get_where(array('role' => 4));
                $data['unit_list'] = json_decode(json_encode($units), TRUE);
                $this->load->view('newtask', $data);
            }
        }
    }/*}}}*/

    public function tasks($category = 1)
    {/*{{{*/
        if ($this->is_login())
        {
            $data['activeonelevel'] = "task";
            $data['activetwolevel'] = "tasks";
            $data['category'] = $category;
            $data['categorys'] = array(
                "1"=>"政府工作报告",
                "2"=>"市委市政府重大决策部署",
                "3"=>"建议提案",
                "4"=>"会议议定事项", 
                "5"=>"领导批示",
                "6"=>"专项督查",
                "7"=>"重点项目"
            );
            $data['check'] = $category;
            //$sql = 'select t.*, u.name as unitName from tbl_task t,tbl_unit u where t.unitId = u.id and t.accept=1 and t.category='.$category.';';
            $sql = 'select t.*, u.name as unitName from tbl_task t,tbl_unit u where t.valid = 1 and t.unitId = u.id and t.category='.$category.' order by t.id desc;';
            $this->load->model('task_model');
            $tasks = $this->task_model->query($sql);
            $data['tasks'] = json_decode(json_encode($tasks), TRUE);
            $this->load->view('tasks', $data);
        }
    }/*}}}*/

    public function recalltask($taskId = 0, $category = 0)
    {/*{{{*/
        if ($taskId > 0)
        {
            $this->load->model('task_model');
//            $sql = 'delete from tbl_trace where taskId ='.$taskId.';';
            $this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
            //$rows = $this->task_model->excute($sql);
            $sql = 'update tbl_trace set valid = 0 where taskId ='.$taskId.';';
            $rows = $this->task_model->excute($sql);
            if ($rows)
            {
                echo '<script>alert("撤回成功!"); window.location.href="'.site_url("tasks/").$category.'"</script>';
                return;
            }
        }
        echo '<script>alert("撤回失败!"); window.location.href="'.site_url("tasks/").$category.'"</script>';
    }/*}}}*/

	public function finishtask($taskId = 0, $category = 0)
	{/*{{{*/
        if ($taskId > 0)
        {
            $this->load->model('task_model');
//            $sql = 'delete from tbl_trace where taskId ='.$taskId.';';
            $rows = $this->task_model->update_where(array('progress' => 5), array('id' => $taskId));
            //$rows = $this->task_model->excute($sql);
            //$sql = 'update tbl_trace set valid = 0 where taskId ='.$taskId.';';
            //$rows = $this->task_model->excute($sql);
            if ($rows)
            {
                echo '<script>alert("调度完成成功!"); window.location.href="'.site_url("tasks/").$category.'"</script>';
                return;
            }
        }
        echo '<script>alert("调度完成失败!"); window.location.href="'.site_url("tasks/").$category.'"</script>';

	}/*}}}*/

    public function tasktrace($category = 0)
    {/*{{{*/
		$category = $this->input->post('category');
		if (!isset($category) || $category == null || $category <= 0)
		{
			$category = 1;
		}
        if ($this->is_login())
        {
            $data['activeonelevel'] = "task";
            $data['activetwolevel'] = "image";
            $data['categorys'] = array(
                "1"=>"政府工作报告",
                "2"=>"市委市政府重大决策部署",
                "3"=>"建议提案",
                "4"=>"会议议定事项", 
                "5"=>"领导批示",
                "6"=>"专项督查",
                "7"=>"重点项目"
            );
            $data['check'] = $category;
            //$sql = 'select t.*, u.name as unitName from tbl_task t,tbl_unit u where t.unitId = u.id and t.accept=1 and t.category='.$category.';';
			
            $sql = "select * from tbl_task where id in (select taskId from tbl_trace where attachment != '' group by taskId) and category = ".$category.";";
            $this->load->model('task_model');
            $tasks = $this->task_model->query($sql);
            foreach($tasks as $task)
            {
                $sql = "select * from tbl_trace where taskId = ".$task->id." and attachment !='';";
                $traces = $this->task_model->query($sql);
                $traces = isset($traces) && count($traces) ? $traces : array();
                $task->traces = $traces;
            }
            $data['tasks'] = json_decode(json_encode($tasks), TRUE);
            $this->load->view('tasktrace', $data);
        }

    }/*}}}*/

    public function exportTraceImg($traceId)
    {/*{{{*/
        $sql = 'select attachment from tbl_trace where id = '.$traceId.';';
        $this->load->model('task_model');
        $fileNames = $this->task_model->query($sql);
        $fileNames = count($fileNames) ? $fileNames[0] : array('attachment' => '');
        $fileString = $fileNames->attachment;
        $names = explode(',',$fileString); 
        $this->load->helper('file');
        $data = array();
        foreach($names as $name)
        {
            $data[$name] = read_file('./uploads/'.$name);
        }

        $this->load->library('zip');
        $this->zip->add_data($data);
        // Write the zip file to a folder on your server. Name it "my_backup.zip"
        $this->zip->archive('./uploads/backup.zip');
        
        // Download the file to your desktop. Name it "my_backup.zip"
        $this->zip->download('backup.zip');

    }/*}}}*/

    public function newUnit()
    {/*{{{*/
        if ($this->is_login())
        {
            $data = array(
                'name' => $this->input->post('name'),
                'role' => 4
            );
            if (!in_array(null, array_values($data)))
            {
                if (count($_FILES) > 0 && array_key_exists('userfile', $_FILES))
                {
                    $file = $this->do_upload(0, 'userfile');
                    $data['logo'] = $file ? $file : '';
                }
                $this->load->model('unit_model', '', true);
                $id = $this->unit_model->create_id($data);
                $parentIds = $this->input->post('parentIds');
                if (count($parentIds)) 
                {
                    $this->load->model('unitrelation_model');
                    foreach($parentIds as $parentId)
                    {
                        $this->unitrelation_model->create_id(array('unitId' => $id, 'parentId' => $parentId));
                    }
                }

                echo '<script>alert("创建部门成功!"); window.location.href="newunit"</script>';
            }
            else
            {
                $data['activeonelevel'] = "unit";
                $data['activetwolevel'] = "add";
                $this->load->model('unit_model', '', true);
                $units = $this->unit_model->get_where(array('role' => 2));
                $data['units'] = json_decode(json_encode($units), TRUE);
                $this->load->view('newunit', $data);
            }
        }
    }/*}}}*/

	public function allUnits()
	{/*{{{*/
		$data['activeonelevel'] = "unit";
		$data['activetwolevel'] = "all";

		$this->load->model('unit_model', '', true);
		$units = $this->unit_model->get_where(array('role' => 4));
		$data['units'] = json_decode(json_encode($units), TRUE);

		$this->load->view('units', $data);
	}/*}}}*/

	public function ajaxDelUnit($unitId = 0)
	{/*{{{*/
        if ($unitId > 0)
        {
			$this->load->model('unit_model', '', true);
            $sql = 'delete from tbl_unit where id='.$unitId.';';
            //$this->task_model->update_where(array('valid' => 0), array('id' => $taskId));
            $rows = $this->unit_model->excute($sql);
            //$sql = 'update tbl_trace set valid = 0 where taskId ='.$taskId.';';
            //$rows = $this->task_model->excute($sql);
            if ($rows)
            {
                echo '<script>alert("删除成功!"); window.location.href="'.site_url("allunits").'"</script>';
                return;
            }
        }
        echo '<script>alert("删除失败!"); window.location.href="'.site_url("allunits").'"</script>';
	}/*}}}*/

    /**修改部门**/
    public function units()
    {/*{{{*/
        if ($this->is_login())
        {
            $data['activeonelevel'] = "unit";
            $data['activetwolevel'] = 'edit';
            $this->load->model('unit_model');
            $banners = $this->unit_model->get_where(array('availably' => 1));
            $data['units'] = json_decode(json_encode($banners), TRUE);
            $this->load->view('banners', $data);
        }

    }/*}}}*/

    public function editunit()
    {/*{{{*/
        $data['activeonelevel'] = "user";
        $data['activetwolevel'] = 'edit';
        $data['title'] = '修改用户';
        $data['verify'] = -1;
        $sql = 'select u.*, t.name as unitName from tbl_user u, tbl_unit t where u.unitId = t.id and u.verify = 1 order by u.unitId;';
        $this->load->model('user_model');
        $users = $this->user_model->query($sql);
        $data['users'] = json_decode(json_encode($users), TRUE);
        $this->load->view('users', $data);
    }/*}}}*/

    public function newBanner()
    {/*{{{*/
        if ($this->is_login())
        {
            $data = array(
                'title' => $this->input->post('title'),
                'link' => $this->input->post('link'),
            );
            if (!in_array(null, array_values($data)))
            {
                if (count($_FILES) > 0 && array_key_exists('userfile', $_FILES))
                {
                    $file = $this->do_upload(1, 'userfile');
                    $data['image'] = $file ? $file : '';
                }
                $this->load->model('banner_model', '', true);
                $id = $this->banner_model->create_id($data);
                echo '<script>alert("添加Banner成功!"); window.location.href="newbanner"</script>';
            }
            else
            {
                $data['activeonelevel'] = "banner";
                $data['activetwolevel'] = "add";
                $this->load->model('unit_model');
                $units = $this->unit_model->get_where(array('role' => 2));
                $data['units'] = json_decode(json_encode($units), TRUE);
                $this->load->view('newbanner', $data);
            }
        }
    }/*}}}*/

    public function newNotification()
    {/*{{{*/
        if ($this->is_login())
        {
            $data = array(
                'title' => $this->input->post('title'),
                'content' => $this->input->post('content'),
//                'unitId' => $this->input->post('unitid'),
            );
            if (!in_array(null, array_values($data)))
            {
//                if (count($_FILES) > 0 && array_key_exists('userfile', $_FILES))
//                {
//                    $file = $this->do_upload(1, 'userfile');
//                    $data['image'] = $file ? $file : '';
//                }

                $unitIds = $this->input->post('unitIds');
                if (isset($unitIds) && count($unitIds)) 
                {
                    $this->load->model('message_model', '', true);
                    require_once 'Jpush.php';
                    $jpush = Jpush::getInstance();
                    foreach($unitIds as $unitId)
                    {
                        $data['unitId'] = $unitId;
                        $id = $this->message_model->create_id($data);
                        $tag = $data['unitId'] == -1 ? 'all' : $data['unitId'];
                        $jpush->push($data['title'], array('id' => $id, 'type' => 1), $tag);
                    }
                    echo '<script>alert("发布通知成功!"); window.location.href="newnotification"</script>';
                }
                else
                {
                    $data['activeonelevel'] = "notification";
                    $data['activetwolevel'] = "new";
                    $this->load->model('unit_model');
                    $units = $this->unit_model->get_where(array('role' => 4));
                    $data['unit_list'] = json_decode(json_encode($units), TRUE);
                    $this->load->view('newnotification', $data);
                }

            }
            else
            {
                $data['activeonelevel'] = "notification";
                $data['activetwolevel'] = "new";
                $this->load->model('unit_model');
                $units = $this->unit_model->get_where(array('role' => 4));
                $data['unit_list'] = json_decode(json_encode($units), TRUE);
                $this->load->view('newnotification', $data);
            }
        }
    }/*}}}*/

	public function messages()
	{/*{{{*/
        if ($this->is_login())
        {
			$data['activeonelevel'] = "notification";
			$data['activetwolevel'] = "all";
            $this->load->model('message_model');
			$sql = 'select m.*, u.name as unitName from tbl_message m left join tbl_unit u on m.unitid = u.id;';
            $messages = $this->message_model->query($sql);
            $data['messages'] = json_decode(json_encode($messages), TRUE);
            $this->load->view('messages', $data);
        }
	}/*}}}*/

    public function verifyUsers()
    {/*{{{*/
        $data['activeonelevel'] = "user";
        $data['activetwolevel'] = 'verify';
        $data['title'] = '需审核的用户';
        $data['verify'] = 0;
        $sql = 'select u.*, t.name as unitName from tbl_user u, tbl_unit t where u.unitId = t.id and u.verify = 0 order by u.unitId;';
        $this->load->model('user_model');
        $users = $this->user_model->query($sql);
        //这里取了全部user 需要增加条件只取未审核user
        $data['users'] = json_decode(json_encode($users), TRUE);
        $this->load->view('users', $data);
    }/*}}}*/

    public function enableUsers()
    {/*{{{*/
        $data['activeonelevel'] = "user";
        $data['activetwolevel'] = 'unenable';
        $data['title'] = '可收回的用户';
        $data['verify'] = 1;
        $sql = 'select u.*, t.name as unitName from tbl_user u, tbl_unit t where u.unitId = t.id and u.verify = 1 order by u.unitId;';
        $this->load->model('user_model');
        $users = $this->user_model->query($sql);
        //这里取了全部user 需要增加条件只取未审核user
        $data['users'] = json_decode(json_encode($users), TRUE);
        $this->load->view('users', $data);
    }/*}}}*/

    public function editUsers()
    {/*{{{*/
        $data['activeonelevel'] = "user";
        $data['activetwolevel'] = 'edit';
        $data['title'] = '修改用户';
        $data['verify'] = -1;
        $sql = 'select u.*, t.name as unitName from tbl_user u, tbl_unit t where u.unitId = t.id and u.verify = 1 order by u.unitId;';
        $this->load->model('user_model');
        $users = $this->user_model->query($sql);
        $data['users'] = json_decode(json_encode($users), TRUE);
        $this->load->view('users', $data);
    }/*}}}*/

    public function verifyUser($userId)
    {/*{{{*/
        if ($userId > 0)
        {
            $this->load->model('user_model');
            $rows = $this->user_model->update_where(array('verify' => 1), array('id' => $userId));
            if ($rows)
            {
                echo '<script>alert("审核成功!"); window.location.href="'.site_url("verifyusers").'"</script>';
                return;
            }
        }
        echo '<script>alert("审核失败!"); window.location.href="'.site_url("verifyusers").'"</script>';
    }/*}}}*/

    public function disableUser($userId)
    {/*{{{*/
        if ($userId > 0)
        {
            $this->load->model('user_model');
            $rows = $this->user_model->update_where(array('verify' => 2), array('id' => $userId));
            if ($rows)
            {
                echo '<script>alert("收回成功!"); window.location.href="'.site_url("enableusers").'"</script>';
                return;
            }
        }
        echo '<script>alert("收回失败!"); window.location.href="'.site_url("enableusers").'"</script>';
    }/*}}}*/

    public function editUser($userId = 0, $name, $sex, $duty)
    {/*{{{*/
        if (isset($name) && isset($sex) && isset($duty))
        {
            $data = array('name' => urldecode($name), 'sex' => urldecode($sex), 'duty' => urldecode($duty));
            if (!in_array(null, array_values($data)) && $userId > 0)
            {
                $this->load->model('user_model');
                $rows = $this->user_model->update_where($data, array('id' => $userId));
                echo '<script>alert("更新成功!"); window.location.href="'.site_url("editusers").'"</script>';
                return;
            }
        }
        echo '<script>alert("更新失败!"); window.location.href="'.site_url("editusers").'"</script>';
    }/*}}}*/

    public function banners()
    {/*{{{*/
        $data['activeonelevel'] = "banner";
        $data['activetwolevel'] = 'delete';
        $this->load->model('banner_model');
        $banners = $this->banner_model->get_where(array('availably' => 1));
        $data['banners'] = json_decode(json_encode($banners), TRUE);
        $this->load->view('banners', $data);
    }/*}}}*/

    public function deleteBanner($id)
    {/*{{{*/
        if ($id > 0)
        {
            $this->load->model('banner_model');
            $rows = $this->banner_model->update_where(array('availably' => 0), array('id' => $id));
            if ($rows)
            {
                echo '<script>alert("删除成功!"); window.location.href="'.site_url("banners").'"</script>';
                return;
            }
        }
        echo '<script>alert("删除失败!"); window.location.href="'.site_url("banners").'"</script>';
    }/*}}}*/

    public function newVersion()
    {/*{{{*/
        if ($this->is_login())
        {
            if (count($_FILES) > 0 && array_key_exists('userfile', $_FILES))
            {
                $config['upload_path']='./uploads/';
                $config['allowed_types']='*';
                $config['overwrite'] = true;
                $config['encrypt_name'] = false;
                $config['remove_spaces'] = true;
                $this->load->library('upload',$config);

                if($this->upload->do_upload('userfile'))
                {
                    $this->load->model('version_model', '', true);
                    $count = $this->version_model->select_count(array('id >' => 0));
                    if ($count)
                    {
                        $sql = "UPDATE tbl_version SET version = version + 1";
                        $id = $this->version_model->excute($sql);
                    }
                    else
                    {
                        $id = $this->version_model->create_id(array('version' => '1'));
                    }
                    echo '<script>alert("发布成功!"); window.location.href="version"</script>';
                }
                else
                {
                    echo '<script>alert("上传失败!"); window.location.href="version"</script>';
                }
            }
            else
            {
                $data['activeonelevel'] = "version";
                $data['activetwolevel'] = "add";
                $this->load->view('newversion', $data);
            }
        }
    }/*}}}*/

	public function sendmsg($type = 0)
	{/*{{{*/
		if (count($this->input->post())) 
		{
			require "jpush/JSMS.php";
			$appKey = 'fba33a9922ac91da73db53c5';
			$masterSecret = '167250cda8d95bb611e06f24';
			$client = new \JiGuang\JSMS($appKey, $masterSecret);

			$tempIds = array(0, 155662, 155680, 155664);
			$tells = array('加大工作力度', '强化调度');
			$whos = array('你单位负责', '您分管');

			$category = $this->input->post('category');
			$phoneStr = $this->input->post('phone');
			$task = $this->input->post('task');
			$whoId = $this->input->post('whoId');

			$phones = explode(",",str_replace("，", ",", $phoneStr));

			foreach($phones as $phone) 
			{
				$temp_para = array('who' => $whos[$whoId],'task' => $task,'tell' => $tells[$whoId]);
				$response = $client->sendMessage($phone, $tempIds[$category], $temp_para);
			}

			$url = site_url("sendmsg");
			echo '<script>alert("发送成功!"); window.location.href="'.$url.'"</script>';
		}
		else
		{
			$data['activeonelevel'] = "msg";
			$data['activetwolevel'] = "new";
			$this->load->view('newmsg', $data);
		}
	}/*}}}*/

}
?>

