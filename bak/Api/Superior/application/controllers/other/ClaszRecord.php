<?php
class ClaszRecord extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $id = $this->claszrecord_model->create_id($this->input->post());
        $result = array('id' => $id);


        // Include library
        $this->load->library('mongo_db');

        // Create new connection
        $this->mongo_db->connect();

        // Execute select query
        $result = $this->mongo_db->set('zan', '180000')->where('id', "1")->update('store');
        var_dump($result);

        $result = $this->mongo_db->where('id', '1')->get('store');
        var_dump($result);

        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

    public function update()
    {/*{{{*/
        $data = $this->input->json();
        if (array_key_exists('id', $data)) {
            $id = $this->claszrecord_model->update_where($data, array('id' => $data['id']));
            $result = array('id' => $id);
            $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
        }else {
            $this->set_content(-1, '提交失败', '缺少参数');
        }
    }/*}}}*/

    public function remove()
    {
        $data = $this->input->json();
        if (array_key_exists('ids', $data))
        {
            if (count($data['ids'])) $this->claszrecord_model->deleteBatch('id',  $data['ids']);     
            
            $this->load->model('node_model');
            foreach ($data['ids'] as $id) {
                $id = $this->node_model->update_where(array('status' =>  0), array('id' => $id));
            }

            $this->set_content(0, '删除成功', '');
        }
        else
        {
            $this->set_content(0, '删除失败', '');
        }
    }

    public function updateById($claszrecordId = 0)
    {/*{{{*/
        $data = $this->input->json();
        $id = $this->claszrecord_model->update_where($data, array('id' => $claszrecordId));
        $result = array('id' => $id);

        $sql = 'update tbl_student set  point = point+'.$data['point'].' where id ='.$data['studentId'];
        $this->claszrecord_model->excute($sql);

        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

    public function getClaszRecords() {
        $records = $this->claszrecord_model->select_where_with_limit_order(array(), array(), 0, 'id');
        $result = array("list" => $records, "pagination" => array("total" => count($records)));
        $this->set_content(0, '获取成功', $result);
    }

    public function getClaszRecordsByStudent($studentId = 0)
    {/*{{{*/
        $records = $this->claszrecord_model->getRecordsBy($studentId);
        $this->set_content(0, '获取成功', $records);
    }/*}}}*/

    public function getSignClaszrecord($studentId) {
        $record = $this->claszrecord_model->get_where_with_order_and_limit(array('studentId' => $studentId, 'closure' => 1), 'id', 1);
        if ($record)
        {
            $claszTime = $record['claszTime'];
            $record['day'] = substr($claszTime, 0, 10);
            $record['time'] = preg_replace('# #','', trim(substr($claszTime, 11), "()")) ;
        }
        $this->set_content(0, '获取成功', $record ? $record : array('id' => 0));
    }

    public function signClasz($id) {
        $rows = $this->claszrecord_model->update_where(array('closure' => 2), array('id' => $id));
        $this->set_content(0, '获取成功', $rows);
    }

    public function getCommentClasz($teacherId)
    {
        $sql = 'select n.*, s.logo, s.name as studentName, s.sex , ROUND(DATEDIFF(CURDATE(),  s.birthday)/365.2422) as age from tbl_claszrecord as n left join tbl_student as s on s.id = n.studentId where n.closure = 1 and n.teacherId = '.$teacherId.';'; 

        $result = $this->claszrecord_model->query_array($sql);

        $this->set_content(0, '获取成功', $result);

    }

    public function getClaszRecordsByTeacher($teacherId)
    {
        $sql = 'select n.*, s.logo, s.name as studentName, s.sex , ROUND(DATEDIFF(CURDATE(),  s.birthday)/365.2422) as age from tbl_claszrecord as n left join tbl_student as s on s.id = n.studentId where n.teacherId = '.$teacherId.';'; 

        $result = $this->claszrecord_model->query_array($sql);

        $history = array();
        $plan = array();

        $vips = 0;
        foreach ($result as $record) {
            if ($record['closure'] == 1) $plan[] = $record;
            if ($record['closure'] == 2) $history[] = $record;
            if ($record['type'] == 1 && $record['closure'] == 2) $vips++;
        }

        $data = array('sum' => count($history), 'vip' => $vips, 'free' => count($history) - $vips, 'plan' => count($plan));
        $result = array('history' => $history, 'plan' => $plan, 'data' => $data);

        $this->set_content(0, '获取成功', $result);

    }
}
?>