<?php
class Unit extends CA_Controller {

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

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

    public function createUnit($name = '')
    {/*{{{*/
        //post 获取参数
        //$data = array('name' => $this->input->post('name'));
        $id = $this->unit_model->create_id(array('name'=>$name));
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '创建成功' : '创建失败', '');
    }/*}}}*/

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
        $units = $this->unit_model->get_where(array('role > ' => 2));
        $this->set_content(0, '获取成功', $units);
    }/*}}}*/
}
?>
