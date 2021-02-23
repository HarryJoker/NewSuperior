<?php
class Admin extends CA_Controller {
    function __construct()
    {/*{{{*/
        parent::__construct();
    }/*}}}*/


    public function create()
    {/*{{{*/
        $data = $this->input->json();
        $id = $this->admin_model->create_id($data);
        $data['id'] = $id;
        $this->set_content($id > 0 ? 0 : -1, $id > 0 ? '创建成功' : '创建失败', $data);
    }/*}}}*/

    public function update()
    {/*{{{*/
        
        $data = $this->input->json();
        if (count($data) && array_key_exists('id', $data))
        {
            $this->admin_model->update_where($data,  array('id' => $data['id']));
            $data = $this->admin_model->get_where(array('id' => $data['id']));
            $this->set_content(0, '更新成功', $data);
        }
        else
        {
            $this->set_content( -1 , '更新失败', '');
        }
    }/*}}}*/

    public function remove()
    {
        $data = $this->input->json();
        if (array_key_exists('ids', $data))
        {
            if (count($data['ids'])) $this->admin_model->deleteBatch('id',  $data['ids']);        
            $this->set_content(0, '删除成功', '');
        }
        else
        {
            $this->set_content(0, '删除失败', '');
        }
    }

    public function currentUser() {
        $user = $this->admin_model->get_where(array('id' => 1));
        $this->set_content(0, '获取成功', $user);
    }   

    public function users()
    {/*{{{*/
        // $data = $this->input->json();
        $articles = $this->admin_model->result();
        $this->set_content(0, '获取成功', $articles);

        $count = $this->admin_model->result_count();

        $result = array("list" => $articles, "pagination" => array("total" => $count));

        $this->set_content(0, '获取成功', $result);

    }/*}}}*/

    public function auth_routes() {
        $auth = '{"/form/advanced-form":{"authority":["admin","user"]}}';
        // $auth = "{'/form/advanced-form':{'authority':['admin','user']}}";
        // $result = json_decode($auth, true);
        // var_dump($result);
        $this->output->set_content_type('application/json')->set_output($auth);
    }

    public function fake_chart_data() {
        $content = '{"visitData":[{"x":"2019-02-26","y":7},{"x":"2019-02-27","y":5},{"x":"2019-02-28","y":4},{"x":"2019-03-01","y":2},{"x":"2019-03-02","y":4},{"x":"2019-03-03","y":7},{"x":"2019-03-04","y":5},{"x":"2019-03-05","y":6},{"x":"2019-03-06","y":5},{"x":"2019-03-07","y":9},{"x":"2019-03-08","y":6},{"x":"2019-03-09","y":3},{"x":"2019-03-10","y":1},{"x":"2019-03-11","y":5},{"x":"2019-03-12","y":3},{"x":"2019-03-13","y":6},{"x":"2019-03-14","y":5}],"visitData2":[{"x":"2019-02-26","y":1},{"x":"2019-02-27","y":6},{"x":"2019-02-28","y":4},{"x":"2019-03-01","y":8},{"x":"2019-03-02","y":3},{"x":"2019-03-03","y":7},{"x":"2019-03-04","y":2}],"salesData":[{"x":"1月","y":464},{"x":"2月","y":623},{"x":"3月","y":226},{"x":"4月","y":1165},{"x":"5月","y":740},{"x":"6月","y":774},{"x":"7月","y":443},{"x":"8月","y":507},{"x":"9月","y":872},{"x":"10月","y":217},{"x":"11月","y":867},{"x":"12月","y":291}],"searchData":[{"index":1,"keyword":"搜索关键词-0","count":507,"range":2,"status":1},{"index":2,"keyword":"搜索关键词-1","count":238,"range":9,"status":1},{"index":3,"keyword":"搜索关键词-2","count":532,"range":88,"status":1},{"index":4,"keyword":"搜索关键词-3","count":324,"range":33,"status":1},{"index":5,"keyword":"搜索关键词-4","count":326,"range":63,"status":0},{"index":6,"keyword":"搜索关键词-5","count":398,"range":61,"status":0},{"index":7,"keyword":"搜索关键词-6","count":170,"range":12,"status":0},{"index":8,"keyword":"搜索关键词-7","count":526,"range":61,"status":1},{"index":9,"keyword":"搜索关键词-8","count":507,"range":31,"status":1},{"index":10,"keyword":"搜索关键词-9","count":648,"range":43,"status":0},{"index":11,"keyword":"搜索关键词-10","count":317,"range":93,"status":1},{"index":12,"keyword":"搜索关键词-11","count":732,"range":59,"status":1},{"index":13,"keyword":"搜索关键词-12","count":357,"range":76,"status":1},{"index":14,"keyword":"搜索关键词-13","count":242,"range":45,"status":0},{"index":15,"keyword":"搜索关键词-14","count":463,"range":55,"status":0},{"index":16,"keyword":"搜索关键词-15","count":903,"range":28,"status":1},{"index":17,"keyword":"搜索关键词-16","count":77,"range":85,"status":1},{"index":18,"keyword":"搜索关键词-17","count":439,"range":98,"status":1},{"index":19,"keyword":"搜索关键词-18","count":261,"range":74,"status":0},{"index":20,"keyword":"搜索关键词-19","count":695,"range":2,"status":0},{"index":21,"keyword":"搜索关键词-20","count":814,"range":3,"status":0},{"index":22,"keyword":"搜索关键词-21","count":4,"range":2,"status":0},{"index":23,"keyword":"搜索关键词-22","count":652,"range":48,"status":1},{"index":24,"keyword":"搜索关键词-23","count":283,"range":46,"status":0},{"index":25,"keyword":"搜索关键词-24","count":864,"range":28,"status":0},{"index":26,"keyword":"搜索关键词-25","count":227,"range":4,"status":0},{"index":27,"keyword":"搜索关键词-26","count":136,"range":64,"status":1},{"index":28,"keyword":"搜索关键词-27","count":471,"range":55,"status":0},{"index":29,"keyword":"搜索关键词-28","count":117,"range":87,"status":1},{"index":30,"keyword":"搜索关键词-29","count":497,"range":48,"status":1},{"index":31,"keyword":"搜索关键词-30","count":765,"range":1,"status":0},{"index":32,"keyword":"搜索关键词-31","count":797,"range":72,"status":1},{"index":33,"keyword":"搜索关键词-32","count":27,"range":92,"status":0},{"index":34,"keyword":"搜索关键词-33","count":881,"range":93,"status":1},{"index":35,"keyword":"搜索关键词-34","count":701,"range":91,"status":1},{"index":36,"keyword":"搜索关键词-35","count":122,"range":28,"status":1},{"index":37,"keyword":"搜索关键词-36","count":776,"range":62,"status":1},{"index":38,"keyword":"搜索关键词-37","count":745,"range":87,"status":0},{"index":39,"keyword":"搜索关键词-38","count":238,"range":37,"status":1},{"index":40,"keyword":"搜索关键词-39","count":603,"range":35,"status":0},{"index":41,"keyword":"搜索关键词-40","count":535,"range":22,"status":1},{"index":42,"keyword":"搜索关键词-41","count":513,"range":58,"status":1},{"index":43,"keyword":"搜索关键词-42","count":165,"range":99,"status":1},{"index":44,"keyword":"搜索关键词-43","count":695,"range":66,"status":0},{"index":45,"keyword":"搜索关键词-44","count":196,"range":79,"status":0},{"index":46,"keyword":"搜索关键词-45","count":961,"range":14,"status":0},{"index":47,"keyword":"搜索关键词-46","count":0,"range":52,"status":1},{"index":48,"keyword":"搜索关键词-47","count":560,"range":59,"status":0},{"index":49,"keyword":"搜索关键词-48","count":490,"range":81,"status":0},{"index":50,"keyword":"搜索关键词-49","count":667,"range":72,"status":0}],"offlineData":[{"name":"Stores 0","cvr":0.4},{"name":"Stores 1","cvr":0.9},{"name":"Stores 2","cvr":0.9},{"name":"Stores 3","cvr":0.3},{"name":"Stores 4","cvr":0.7},{"name":"Stores 5","cvr":0.2},{"name":"Stores 6","cvr":0.7},{"name":"Stores 7","cvr":0.1},{"name":"Stores 8","cvr":0.8},{"name":"Stores 9","cvr":0.9}],"offlineChartData":[{"x":1551161518279,"y1":65,"y2":34},{"x":1551163318279,"y1":60,"y2":62},{"x":1551165118279,"y1":95,"y2":67},{"x":1551166918279,"y1":80,"y2":91},{"x":1551168718279,"y1":38,"y2":75},{"x":1551170518279,"y1":38,"y2":106},{"x":1551172318279,"y1":47,"y2":34},{"x":1551174118279,"y1":76,"y2":104},{"x":1551175918279,"y1":104,"y2":54},{"x":1551177718279,"y1":53,"y2":55},{"x":1551179518279,"y1":21,"y2":34},{"x":1551181318279,"y1":107,"y2":107},{"x":1551183118279,"y1":84,"y2":79},{"x":1551184918279,"y1":107,"y2":106},{"x":1551186718279,"y1":47,"y2":95},{"x":1551188518279,"y1":46,"y2":69},{"x":1551190318279,"y1":87,"y2":10},{"x":1551192118279,"y1":59,"y2":55},{"x":1551193918279,"y1":45,"y2":92},{"x":1551195718279,"y1":107,"y2":43}],"salesTypeData":[{"x":"吉他课程","y":4544},{"x":"架子鼓","y":3321},{"x":"小提琴","y":3113},{"x":"钢琴","y":2341},{"x":"萨克斯","y":1231},{"x":"其他","y":1231}],"salesTypeDataOnline":[{"x":"吉他","y":244},{"x":"萨克斯","y":321},{"x":"小提琴","y":311},{"x":"钢琴","y":41},{"x":"古筝","y":121},{"x":"其他","y":111}],"salesTypeDataOffline":[{"x":"钢琴","y":99},{"x":"架子鼓","y":188},{"x":"吉他","y":344},{"x":"古筝","y":255},{"x":"其他","y":65}],"radarData":[{"name":"个人","label":"引用","value":10},{"name":"个人","label":"口碑","value":8},{"name":"个人","label":"产量","value":4},{"name":"个人","label":"贡献","value":5},{"name":"个人","label":"热度","value":7},{"name":"团队","label":"引用","value":3},{"name":"团队","label":"口碑","value":9},{"name":"团队","label":"产量","value":6},{"name":"团队","label":"贡献","value":3},{"name":"团队","label":"热度","value":1},{"name":"部门","label":"引用","value":4},{"name":"部门","label":"口碑","value":1},{"name":"部门","label":"产量","value":6},{"name":"部门","label":"贡献","value":5},{"name":"部门","label":"热度","value":7}]}';

         $this->output->set_content_type('application/json')->set_output($content);
    }
}
?>