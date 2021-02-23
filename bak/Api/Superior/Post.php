<?php 

//phpinfo();
   
    // createLabel();
    // getLabels();

    // createAlbum();
    // getAlbumBy();

    // createCategory();
    // getCategorys();

    // createClasz();
    // getClaszs();

    // createClaszRecord();
    // getClaszRecordBy(1);
    // updateClaszRecord(4);
    
    // createComment();
    // getStoreComments(1);
    // getTeacherComments(1);
    // getStudentComments(1);
    
    // createStore();
    // getStore();

    // createStudent();
    // getStudent();
    // getGoodStudents();

    // createTeacher();
    // getTeacher();
    // getGoodTeachers();
    
    // createBanner();
    // getBanners();
    // updateBanner();


    function createBanner() {
        $action = 'banner/create';
        $post_data['module']         = '4';
        $post_data['content']      = '1982年6月14日出生于辽宁省沈阳市沈河区，中国钢琴演奏者，联合国和平使者，毕业于美国柯蒂斯音乐学院。1997年12月，郎朗与IMG经纪公司签约，开启了职业演出生涯。1999年8月，参加美国芝加哥“拉维尼亚音乐节”世纪庆典明星音乐会，作为替补登台演奏';
        $post_data['title']         = '王晓明参与活动第一名';
        $post_data['src']         = 'http://yooar.com/imgs/kailen.png';
        $res = request_post($action, $post_data);
        print_r($res);
    }

     function getBanners(){
        $action = 'banner/getBanners';
        $res = request_get($action);
        print_r($res);
    }

    function updateBanner($bannerId = 1){
        $action = 'banner/update/'.$bannerId;
        $post_data['module']         = '4';
        $post_data['content']      = '水电费1982年6月14日出生于辽宁省沈阳市沈河区，中国钢琴演奏者，联合国和平使者，毕业于美国柯蒂斯音乐学院。1997年12月，郎朗与IMG经纪公司签约，开启了职业演出生涯。1999年8月，参加美国芝加哥“拉维尼亚音乐节”世纪庆典明星音乐会，作为替补登台演奏';
        $post_data['title']         = '王小明参与活动第一名';
        $post_data['src']         = 'http://yooar.com/imgs/kailen.png';
        $res = request_post($action, $post_data);
        print_r($res);
    }




    function createTeacher() {
        $action = 'teacher/create';
        $post_data['birthday']         = '1987-06-26';
        $post_data['info']      = '1982年6月14日出生于辽宁省沈阳市沈河区，中国钢琴演奏者，联合国和平使者，毕业于美国柯蒂斯音乐学院。1997年12月，郎朗与IMG经纪公司签约，开启了职业演出生涯。1999年8月，参加美国芝加哥“拉维尼亚音乐节”世纪庆典明星音乐会，作为替补登台演奏';
        $post_data['name']         = '王晓明';
        $post_data['logo']         = 'http://yooar.com/imgs/kailen.png';
        $post_data['sex']         = '0';
        $post_data['major']         = '小提琴，大提琴，萨克斯';
        $post_data['teachage']         = '11';
        $res = request_post($action, $post_data);
        print_r($res);
    }

     function getGoodTeachers(){
        $action = 'teacher/getGoodTeachers';
        $res = request_get($action);
        print_r($res);
    }

    function getTeacher($teacherId = 1){
        $action = 'teacher/getTeacher/'.$teacherId;
        $res = request_get($action);
        print_r($res);
    }




    function createStudent() {
        $action = 'student/create';
        $post_data['birthday']         = '1987-06-26';
        $post_data['level']      = '1';
        $post_data['name']         = '王晓明';
        $post_data['logo']         = 'http://yooar.com/imgs/kailen.png';
        $post_data['sex']         = '0';
        $res = request_post($action, $post_data);
        print_r($res);
    }

     function getGoodStudents(){
        $action = 'student/getGoodStudents';
        $res = request_get($action);
        print_r($res);
    }

    function getStudent($studentId = 1){
        $action = 'student/getStudent/'.$studentId;
        $res = request_get($action);
        print_r($res);
    }
    
    



    function createStore() {
        $action = 'store/create';
        $post_data['address']         = '河北省三河市廊坊市燕郊湾仔城商业区6号楼310';
        $post_data['detail']      = '凯伦音乐艺术培训基地位于燕郊湾仔城北门商业街6号楼3层，是燕郊综合类的少儿音乐艺术培训基地。我们有着最专业的教学琴房，有着最优雅的音乐环境、一流的师资团队、创新的一对一及一对多的教学模式、是一家师资力量最雄厚的综合类音乐、器乐的培训基地以及艺考机构。凯伦音乐对每一位学生进行艺术评估，因材施教。让学生们有计划、更快速，更有成效的达到学习目标。凯伦音乐开设钢琴、架子鼓、西洋乐（单簧管、萨克斯、长笛、小号、小提琴、大提琴）吉他、民族乐器（古筝、二胡、竹笛等）声乐、音基一对一及一对多课程。定期举办音乐会，及组织艺术赛事演出，凯伦音乐为央视栏目“群英汇”“CCTV少儿栏目组”指定组织单位。凯伦音乐让学生们学习实践相结合，让孩子们的艺术旅程充满色彩，凯伦音乐艺术培训基地专注艺术，用心相伴，您身边的艺术管家。
孩子是音乐的未来 ，凯伦是成就梦想的摇篮！';
        $post_data['feature']         = '1，综合类音乐培训                 2，师资力量雄厚
3，因材施教                           4，央视节目组织单';
        $post_data['logo']         = 'http://yooar.com/imgs/kailen.png';
        $post_data['name']         = '凯伦音乐培训基地';
        $post_data['phone']         = '0543-89674522';
        $post_data['telphone']         = '13436730367';
        $res = request_post($action, $post_data);
        print_r($res);
    }

     function getStore($storeId = 1){
        $action = 'store/getStore/'.$storeId;
        $res = request_get($action);
        print_r($res);
    }


    function createComment() {

        for ($i=1; $i < 4; $i++) { 
            $action = 'comment/create';
            $post_data['avatar']         = 'www.baidu.com/1.jpg';
            $post_data['content']      = '趣主题聚合志同道合者的互动平台，同好网友聚集在这里交流话题、展示自我、结交朋友。贴吧主题涵盖了娱乐、游戏、小说、地区、生活等各方面';
            $post_data['module']         = $i;
            $post_data['moduleId']         = '1';
            $post_data['name']         = '郭凯';
            $post_data['sex']         = '1';
            $post_data['score']         = '88';
            $res = request_post($action, $post_data);
            print_r($res);
        }
    }

    function getStoreComments($storeId = 0){
        $action = 'comment/getStoreComments/'.$storeId;
        $res = request_get($action);
        print_r($res);
    }

    function getTeacherComments($teacherId = 0){
        $action = 'comment/getStoreComments/'.$teacherId;
        $res = request_get($action);
        print_r($res);
    }

    function getStudentComments($studentId = 0){
        $action = 'comment/getStoreComments/'.$studentId;
        $res = request_get($action);
        print_r($res);
    }




    function createClaszRecord() {
        $action = 'claszrecord/create';
        $post_data['name']         = '第十二节课';
        $post_data['teacherName']      = '郭凯';
        $post_data['teacherId']         = '1';
        $post_data['comment']         = '聪明伶俐，可爱活泼，天赋极强';
        $post_data['type']         = '1';
        $post_data['closure']         = '1';
        $post_data['claszTime']         = '1987-06-26 (10:00 - 10:45)';
        $post_data['score']         = '88';
        $post_data['point']         = '64';
        $post_data['studentId']         = '1';
        $res = request_post($action, $post_data);
        print_r($res);
    }

    function updateClaszRecord($claszrecordId = 1) {
        $action = 'claszrecord/update/'.$claszrecordId;
        $post_data['comment']           = '聪明伶俐，可爱活强';
        $post_data['closure']           = '2';
        $post_data['score']             = '89';
        $post_data['point']             = '7';
        $post_data['labels']            =   implode(",", array('聪明可爱', '认真', '天赋', '毅力'));
        $res = request_post($action, $post_data);
        print_r($res);
    }


    function getClaszRecordBy($studentId = 0){
        $action = 'claszrecord/getClaszRecordsBy/'.$studentId;
        $res = request_get($action);
        print_r($res);
    }



     function createClasz() {
        $action = 'clasz/create';
        $post_data['name']         = '吉他试听课';
        $post_data['detail']      = '使人聪明伶俐，情商提高';
        $post_data['price']         = '11000';
        $post_data['discountprice']         = '8000';
        $post_data['discount']         = '90';
        $post_data['type']         = '1';
        $post_data['categoryName']         = '吉他';
        $post_data['categoryId']         = '1';
        $res = request_post($action, $post_data);
        print_r($res);
    }

    function getClaszs(){
        $action = 'clasz/getClaszs';
        $res = request_get($action);
        print_r($res);
    }
    


     function createCategory() {
        $action = 'category/create';
        $post_data['name']         = '1吉他';
        $post_data['detail']      = '使人聪明伶俐，情商提高';
        $res = request_post($action, $post_data);
        print_r($res);
    }

    function getCategorys(){
        $action = 'category/getCategorys';
        $res = request_get($action);
        print_r($res);
    }


    
    function createAlbum() {
        $action = 'album/create';
        $post_data['type']         = '1';
        $post_data['url']      = 'www.baidu.com';
        $post_data['module']      = '1';
        $res = request_post($action, $post_data);
        print_r($res);
    }

    function getAlbumBy(){
        $action = 'album/getAlbumsBy/1/0';
        $res = request_get($action);
        print_r($res);
    }


    function createLabel() {
        $action = 'label/create';
        $post_data['name']         = '勤奋好学';
        $post_data['description']      = '辛勤努力吃苦耐劳积极性高';
        $res = request_post($action, $post_data);
        print_r($res);
    }

    function getLabels(){
        $action = 'label/getLabels';
        $res = request_get($action);
        print_r($res);
    }



    function testAuth(){
		$url = 'http://115.29.146.185/Client/index.php/user/auth/1/8NCIzhongkeJingzHI23376115121528274490883OD83lkd83dfxx';
        //$url = 'http://localhost/client/index.php/user/auth/1/8NCIzhongkeJingzHI23376115121528274490883OD83lkd83dfxx';
        $res = request_get($url);
        print_r($res);

    }


	/**
     * 模拟get进行url请求
     * @param string $url
     */
    function request_get($action = '') 
	{/*{{{*/
        $domain = "http://train.yooar.com";
        $postUrl = $domain . "/" . $action;
        $ch = curl_init();//初始化curl
        curl_setopt($ch, CURLOPT_URL,$postUrl);//抓取指定网页
        curl_setopt($ch, CURLOPT_HEADER, 0);//设置header
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);//要求结果为字符串且输出到屏幕上
        $data = curl_exec($ch);//运行curl
        curl_close($ch);

        return $data;
    }/*}}}*/


    /**
     * 模拟post进行url请求
     * @param string $url
     * @param array $post_data
     */
    function request_post($action = '', $post_data = array()) 
    {/*{{{*/
        $domain = "http://train.yooar.com";
        $url = $domain . '/' . $action;
        if (empty($url) || empty($post_data)) {
            return false;
        }

        $ch = curl_init();//初始化curl
        curl_setopt($ch, CURLOPT_URL,$url);//抓取指定网页
        curl_setopt($ch, CURLOPT_HEADER, 0);//设置header
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);//要求结果为字符串且输出到屏幕上
        curl_setopt($ch, CURLOPT_POST, 1);//post提交方式
        curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($post_data));
        $data = curl_exec($ch);//运行curl
        curl_close($ch);

        return $data;
    }/*}}}*/


	// /**
 //     * 模拟post进行url请求
 //     * @param string $url
 //     * @param array $post_data
 //     */
 //    function request_post($action = '', $post_data = array()) 
	// {/*{{{*/
 //        $domain = "http://train.yooar.com";
 //        $url = $domain . '/' . $action;
 //        if (empty($url) || empty($post_data)) {
 //            return false;
 //        }

 //        $o = "";
 //        foreach ( $post_data as $k => $v )
 //        {
 //            $o.= "$k=" . urlencode( $v ). "&" ;
 //        }
 //        $post_data = substr($o,0,-1);

 //        $postUrl = $url;
 //        $curlPost = $post_data;
 //        $ch = curl_init();//初始化curl
 //        curl_setopt($ch, CURLOPT_URL,$postUrl);//抓取指定网页
 //        curl_setopt($ch, CURLOPT_HEADER, 0);//设置header
 //        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);//要求结果为字符串且输出到屏幕上
 //        curl_setopt($ch, CURLOPT_POST, 1);//post提交方式
 //        curl_setopt($ch, CURLOPT_POSTFIELDS, $curlPost);
 //        $data = curl_exec($ch);//运行curl
 //        curl_close($ch);

 //        return $data;
 //    }/*}}}*/


    
