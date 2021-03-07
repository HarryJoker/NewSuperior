<?php
class Unit extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    private function makeDoUpload() {
        $logo = '';
        if (count($_FILES))
        {
            $data = $this->do_multiple_upload();
            foreach ($data as $image) { 
                $logo .= $image['file_name'];
            } 
        }
        return $logo;
    }

    public function getAllUnits()
    {/*{{{*/

        //必须写model：大小写与类名一致,补齐后缀(_model)
        $result = $this->unit_model->result();

        //不写model,直接用db
        //$this->db->from('tbl_unit');
        //$this->db->where(array());
        //$result = $this->db->get()->result();

        $this->set_content(0, '获取成功', $result);
    }/*}}}*/

    public function getUnit($unitId = 0) {
        $unit = $this->unit_model->get($unitId);
        $this->set_content(0, '获取成功', $unit);
    }


    public function getUnitsByRole($role = 0)
    {/*{{{*/
        $units = $this->unit_model->get_where(array('role' => $role));
        $this->set_content(0, '获取成功', $units);
    }/*}}}*/


    public function getMapCommonUnits()
    {/*{{{*/
        $units = $this->unit_model->get_where(array('role' => 4));
        $mapUnits = array();
        foreach ($units as $value) {
            $mapUnits[$value['name']] = $value;
        }
        $this->set_content(0, '获取成功', $mapUnits);
    }/*}}}*/


    public function uploadAvatar() {
        if (count($_FILES)) {
            if ($this->do_upload('file')){
                //上传成功
                $data = $this->upload->data();
                $this->set_content(0, '上传成功', $data);
            } else {
                //上传失败
                $data = $this->upload->display_errors();
                $this->set_content(-1, '上传失败', $data);
            }
        } else {
             $this->set_content(-1, '上传失败', '');
        }
    }


    //创建部门（自动生成部门用户）
    public function createUnit()
    {/*{{{*/
        //post 获取参数
        $data = array('name' => $this->input->post('name'), 
                    'logo' => $this->input->post('logo'), 
                    'parentid' => $this->input->post('parentid'),
                    'role' => $this->input->post('role'));
        $id = $this->unit_model->create_id($data);
        $this->load->model('user_model');
        $this->user_model->autoGenerateUnitUsers($id);
        $newUnit = $this->unit_model->getUnitWithUsers($id);
        $this->set_content(0, '创建成功', $newUnit);
    }/*}}}*/


    public function delete($id = 0)
    {
        $result = $this->unit_model->delete(array('id'=>$id));
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '删除成功' : '创建失败', array('result' => $result));
    }

    /**
     * parentId: -1取所有部门
     * */
    public function getUnitsByParentId($parentId = 0)
    {/*{{{*/
        if ($parentId == -1)
        {
            $sql = 'select * from tbl_unit where role = 4;';
            $units = $this->unit_model->query($sql);
        }
        else
        {
            $sql = 'select * from tbl_unit where parentId in (select unitId from tbl_unitrelation where parentId = '.$parentId.') and role = 4;';
            $units = $this->unit_model->query($sql);
        }
        $this->set_content(0, '获取成功', $units);
    }/*}}}*/

    public function getAll()
    {/*{{{*/
        $units = $this->unit_model->get_where(array('role > ' => 0));
        $this->set_content(0, '获取成功', $units);
    }/*}}}*/

    public function getAllUnitWithUser()
    {/*{{{*/
        $units = $this->unit_model->getAllUnitWithUser();
        $this->set_content(0, '获取成功', $units);
    }/*}}}*/

    public function getLeaderUnits() {
        $units = $this->unit_model->get_where(array('role < ' => 3));
        $this->set_content(0, '获取成功', $units);
    }

     public function updateUnit($unitId = 0) {
        $data = $this->input->post();
        $logo = $this->makeDoUpload();
        if (!empty($logo)) {
            $data['logo'] = $logo;
        }
        $this->unit_model->update_where($data, array('id' => $unitId));
        $unit = $this->unit_model->get($unitId);
        $this->set_content(0, '更新成功', $unit);
    }


    //所有所有责任部门的绩效
    public function getDutyUnitScores() {
        $units = $this->unit_model->getDeputyUnits();
        $dutyUnitScores = array();
        foreach ($units as $unit) {
            $categoryScores = $this->unit_model->getUnitScore($unit['id']);
            $synthesises = array_column(array_values($categoryScores), 'synthesis');
            $synthesis = array_sum($synthesises) / count($categoryScores);
            $dutyUnitScores[] = array('synthesis' => $synthesis, 'unit' => $unit);
        }
        //排序
        $dutyUnitScores = $this->arraySort($dutyUnitScores, 'synthesis', SORT_DESC);
        //增加排名
        for ($i=0; $i < count($dutyUnitScores); $i++) { 
            $dutyUnitScores[$i]['rank'] = $i + 1;
        }

        $this->set_content(0, '获取成功', $dutyUnitScores);
    }




    /**
     * 二维数组根据某个字段排序
     * @param array $array 要排序的数组
     * @param string $keys   要排序的键字段
     * @param string $sort  排序类型  SORT_ASC     SORT_DESC 
     * @return array 排序后的数组
     */
    function arraySort($array, $keys, $sort = SORT_DESC) {
        $keysValue = [];
        foreach ($array as $k => $v) {
            $keysValue[$k] = $v[$keys];
        }
        array_multisort($keysValue, $sort, $array);
        return $array;
    }


    public function getUnitScore($unitId = 0) {
        if ($unitId) {
            $unitScores = $this->unit_model->getUnitScore($unitId);
            $this->set_content(0, '获取成功', $unitScores);
        } else {
            $this->set_content(-1, '请检查部门参数', array('unitId' => $unitId));
        }
        
    }


    public function getUnitScoreByCategory($category = 0, $unitId = 0) {
        if ($category && $unitId) {
            $unitCategoryScore = $this->unit_model->getUnitScoreByCategory($category, $unitId);
            $this->set_content(0, '获取成功', $unitCategoryScore);
        } else {
            $this->set_content(0, '请检查参数', array('id' => 0));
        }
        
    }
}
?>
