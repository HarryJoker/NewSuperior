<?php
class Album extends CA_Controller {

    private $accessKey = 'rioPBHsKpDgTXNpw8IGHOecdg6DMWaAO7_L3_7EB';
    private $secretKey = 'ue9VmhBrgefxMDq_Xxp3bRpXe8ufbuNsOKmhnQDD';

    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $data = $this->input->json();
        // $data = array(
        //     'type' => $this->input->post('type', 0),
        //     'url' => $this->input->post('url', ''), 
        //     'path' => $this->input->post('url', ''), 
        //     'module' => $this->input->post('module', 0), 
        //     'moduleId' => $this->input->post('moduleId', 0), 
        // );

        $id = $this->album_model->create_id($data);
        $result = array('id' => $id);
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '提交成功' : '提交失败', $result);
    }/*}}}*/

    public function getAlbumsBy($module = 0, $moduleId = 0)
    {/*{{{*/
        $where = array('valid' => 0);
        if ($module > 0) $where['module'] = $module;
        if ($moduleId > 0) $where['moduleId'] = $moduleId;
        $albums = $this->album_model->select_where($where);
        foreach ($albums as &$album) {
            $album['uid'] = $album['id'];
            $album['status'] = 'done';
        }

        $auth = new Qiniu\Auth($this->accessKey, $this->secretKey);
        $token = $auth->uploadToken('test');

        $result = array('list' => $albums, 'token' => (array('token' => $token)));

        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    public function delete($id = 0)
    {/*{{{*/
        if ($id) 
        {
            $this->album_model->delete(array('id' => $id));
            $this->set_content(0, '删除成功', '');
        }
        else
        {
            $this->set_content(-1, '参数错误', '');
        }
    
    }/*}}}*/

}
?>