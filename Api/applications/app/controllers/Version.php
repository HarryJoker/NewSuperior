<?php
class Version extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/

    public function getVersion()
    {/*{{{*/
        $result = $this->version_model->get_where(array('id' => '1'));
        $result = count($result) ==  1 ? $result[0] : array('version' => '0');
        $this->set_content(0, '获取成功', $result);
    }/*}}}*/


    public function getNewVersion()
    {/*{{{*/
        $version = $this->input->post('version');
        if (isset($version) && $version) {
            $versionResult = $this->version_model->getNewVersion();

            $currentVersion = intval(str_replace('.', '', $versionResult['new_version']));
            $clientVersion = intval(str_replace('.', '', $version));

            $result['update'] =  $currentVersion > $clientVersion ? "Yes" : "No";
            $result['new_version'] = $versionResult['new_version'];
            $result['apk_file_url'] = $versionResult['apk_file_url'];
            $result['update_log'] =  $versionResult['update_log'];
            $result['target_size'] = $versionResult['target_size'].'M';
            $result['constraint'] = true;
            echo json_encode($result);
        } else {
            $result['update'] = "No";
            echo json_encode($result);
        }
        
    }/*}}}*/


    public function getVersions() {
        $result = $this->version_model->result();
        $this->set_content(0, '获取成功', $result);
    }



    public function newVersion()
    {/*{{{*/
        $id = $this->version_model->create_id($this->input->post());
        $version = $this->version_model->get($id);
        $this->set_content(0, '发布成功', $version);
    }/*}}}*/




    // public function newVersion()
    // {/*{{{*/

    //     if (count($_FILES) > 0 && array_key_exists('apk', $_FILES))
    //     {
    //         $config['upload_path']='./uploads/';
    //         $config['allowed_types']='*';
    //         $config['overwrite'] = true;
    //         $config['encrypt_name'] = false;
    //         $config['remove_spaces'] = true;
    //         $config['file_name'] = 'app.apk';
    //         $this->load->library('upload',$config);
    //         if($this->upload->do_upload('apk')) {
    //             $data = $this->input->post();
    //             $id = $this->version_model->create_id($data);
    //             $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '发布成功' : '发布失败', '');
    //         } else {
    //             $this->set_content(-1, 'Apk上传失败', '');
    //         }
    //     } 
    //     else
    //     {
    //         $this->set_content(-1, '发布版本失败', '');
    //     }
    // }/*}}}*/

}
?>
