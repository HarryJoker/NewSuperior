<?php
class CA_Model extends CI_Model {

    private $name; 

    function __construct()
    {/*{{{*/
        parent::__construct();
        //$this->load->database();
        $this->name = str_replace('_model', '', strtolower(get_class($this))); 
    }/*}}}*/
    
    function excute($sql)
    {/*{{{*/
        $this->db->query($sql);
        return $this->db->affected_rows();
    }/*}}}*/

    function query($sql)
    {/*{{{*/
        $query = $this->db->query($sql);
        return $query->result_array();
    }/*}}}*/


    function queryArray($sql)
    {/*{{{*/
        $query = $this->db->query($sql);
        return $query->result_array();
    }/*}}}*/

    function queryRow($sql)
    {/*{{{*/
        $query = $this->db->query($sql);
        return $query->row_array();
    }/*}}}*/

    function deleteRecord($where = array())
    {/*{{{*/
        if (count($where) == 0) return false;
        $this->db->where($where);
        return $this->db->update($this->name, array('state' => '1')); 
    }/*}}}*/

    function delete($where = array())
    {/*{{{*/
        if (count($where) == 0) return false;
        $this->db->where($where);
        $this->db->delete($this->name); 
        return $this->db->affected_rows();
    }/*}}}*/

    function getDistanceField($lon, $lat)
    {/*{{{*/ return "ROUND((2 * 6378.137* ASIN(SQRT(POW(SIN(PI()*(".$lat."- Y(geo))/360),2)+COS(PI()*".$lat."/180)* COS(Y(geo) * PI()/180)*POW(SIN(PI()*(".$lon."-X(geo))/360),2)))), 2) as distance"; }/*}}}*/
    
    function getDistanceFieldByName($lon, $lat, $tableName)
    {/*{{{*/
        return "ROUND((2 * 6378.137* ASIN(SQRT(POW(SIN(PI()*(".$lat."- Y(".$tableName.".geo))/360),2)+COS(PI()*".$lat."/180)* COS(Y(".$tableName.".geo) * PI()/180)*POW(SIN(PI()*(".$lon."-X(".$tableName.".geo))/360),2)))), 2) as distance";
    }/*}}}*/

    function result()
    {/*{{{*/
        $this->db->from($this->name);
        return $this->db->get()->result_array();
    }/*}}}*/

    function result_count($where = array())
    {/*{{{*/
        $this->db->where($where);
        $this->db->from($this->name);
        return $this->db->count_all_results();
    }/*}}}*/

    function get($id = 0)
    {/*{{{*/
        if ($id <= 0) return array();
        $query = $this->db->get_where($this->name, array('id' => $id));
        $row =$query->row_array();
        return isset($row) ? $row : array();
    }/*}}}*/

    function get_where($where = array())
    {/*{{{*/
        return empty($where) ? array() : $this->db->get_where($this->name, $where)->result_array();
    }/*}}}*/

    function get_where_with_order($where = array(), $order = 'id')
    {/*{{{*/
        $this->db->order_by($order, 'desc');
        return empty($where) ? array() : $this->db->get_where($this->name, $where)->result_array();
    }/*}}}*/

    function get_where_with_order_for_asc($where = array(), $order = 'id')
    {/*{{{*/
        $this->db->order_by($order, 'asc');
        return empty($where) ? array() : $this->db->get_where($this->name, $where)->result_array();
    }/*}}}*/

    function get_in($inName = '', $ins = array())
    {/*{{{*/
        if (empty($ins)) return array();
        $this->db->where_in($inName, $ins);
        $query = $this->db->get($this->name);
        return $query->result();
    }/*}}}*/

    function get_where_in($inName = '', $ins = array(), $where = array())
    {/*{{{*/
        if (empty($ins)) return array();
        $this->db->where($where);
        $this->db->where_in($inName, $ins);
        $query = $this->db->get($this->name);
        return $query->result();
    }/*}}}*/

    function create($record = array())
    {/*{{{*/
        return empty($record) ? false : $this->db->insert($this->name, $record);
    }/*}}}*/

    function create_id($record = array())
    {/*{{{*/
        $result = $this->create($record);
        return $result ? $this->db->insert_id() : 0; 
    }/*}}}*/

    function create_batch($data = array())
    {/*{{{*/
        $this->db->insert_batch($this->name, $data);
        return $this->db->affected_rows();

    }/*}}}*/

    function create_escape($record = array())
    {/*{{{*/
        foreach($record as $key=>$value) $this->db->set($key, $value, $key!='geo');
        $this->db->insert($this->name);
        return $this->db->insert_id(); 
    }/*}}}*/
    
    function update($data = array())
    {/*{{{*/
        return $this->update_where($data); 
    }/*}}}*/

    function update_where($data = array(), $where = array())
    {/*{{{*/
        if (empty($data)) return 0;
        $this->db->update($this->name, $data, $where);
        return $this->db->affected_rows(); 
    }/*}}}*/

    function select($fields = array())
    {/*{{{*/
        return $this->select_where($fields);
    }/*}}}*/

    function select_count($where = array())
    {/*{{{*/
        $this->db->where($where);
        $this->db->from($this->name);
        return $this->db->count_all_results();
    }/*}}}*/

    function select_where($fields = array(), $where = array())
    {/*{{{*/
        return $this->select_where_with_limit($fields, $where, 0);
    }/*}}}*/

    function select_where_join($fields = array(), $where = array(), $joinName)
    {/*{{{*/
        $this->db->select(implode(',', $fields));
        $this->db->where($where);
        $this->db->from('tbl_user');
        $this->db->join($joinName, $joinName.'.userid = tbl_user.id');
        $query = $this->db->get();
        return $query->result();
    }/*}}}*/

    function select_where_with_limit($fields = array(), $where = array(), $limit = 0)
    {/*{{{*/
        return $this->select_where_with_limit_order($fields, $where, $limit,null);
    }/*}}}*/

    function select_where_with_limit_order($fields = array(), $where = array(), $limit = 0, $order='')
    {/*{{{*/
        if (!empty($fields)) $this->db->select(implode(',', $fields));
        if ($order != null && count($order) > 0) $this->db->order_by($order, 'desc');
        $this->db->where($where);
        if ($limit > 0) 
        {
            $this->db->limit($limit);
        }
        $query = $this->db->get($this->name);
        return $query->result();
    }/*}}}*/
    
    function select_by_primary($fields = array(), $primary = array())
    {/*{{{*/
        $result = $this->select_where($fields, $primary);
        return isset($result) && count($result) == 1 ? $result[0] : array();
    }/*}}}*/

    function select_where_in($fields = array(), $inName = '', $ins = array(), $where = array())
    {/*{{{*/
        return $this->select_where_in_with_limit($fields, $inName, $ins, $where, 0);
    }/*}}}*/

    function select_where_in_with_limit($fields = array(), $inName = '', $ins = array(), $where = array(), $limit = 0)
    {/*{{{*/
        if (empty($ins)) return array();
        if (false == empty($fields)) 
        {
            $this->db->select(implode(',', $fields));
        }
        if (false == empty($where))
        {
            $this->db->where($where);
        }
        if ($limit > 0) 
        {
            $this->db->limit($limit);
        }
        $this->db->where_in($inName, $ins);
        $query = $this->db->get($this->name);
        return $query->result();
    }/*}}}*/

}
?>
