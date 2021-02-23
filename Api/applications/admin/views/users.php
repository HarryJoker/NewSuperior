<?php include dirname(__FILE__) . '/header.php' ?>
<body>
<section id="container">
    <!--sidebar end-->
    <?php include dirname(__FILE__) . '/left.php' ?>
    <!--main content start-->
    <section class="main-content-wrapper">
        <section id="main-content">
            <div class="row">
                <div class="col-md-12">
                    <h1 class="h2"><?php echo $title ?></h1>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th>姓名</th>
                                    <th>性别</th>
                                    <th>电话</th>
                                    <th>部门</th>
                                    <th>职位</th>
                                    <th>操作</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                foreach ($users as $user) {
                                    $url = ($verify == -1 ? "edituser/" : ($verify == 0 ? "verifyuser/" : "disableuser/")).$user['id'];
                                    ?>
                                        <tr <?php echo 'id="'.$user['id'].'"' ?>>
                                        <td <?php echo ($verify == -1 ? 'ondblclick="ShowElement(this)"' : '').' id=name' ?>><?=$user['name']?></td>
                                        <td <?php echo ($verify == -1 ? 'ondblclick="ShowElement(this)"' : '').' id=sex' ?>><?=$user['sex']?></td>
                                        <td><?=$user['phone']?></td>
                                        <td ><?=$user['unitName']?></td>
                                        <td <?php echo ($verify == -1 ? 'ondblclick="ShowElement(this)"' : '').' id=duty' ?>><?=$user['duty']?></td>
                                        <?php if ($verify == -1) { ?>
                                        <td><a onclick=<?php echo "updateClick(".$user['id'].")" ?> href="javascript:void(0)">更新</a></td>
                                        <?php } else { ?>
                                        <td><a href="javascript:if(confirm('确认帐号操作么?'))location='<?php echo site_url($url)?>'"><?php echo ($verify == -1 ? '更新' : ($verify == 0 ? '通过': '收回'))?></a></td> 
                                        <?php } ?>

                                    </tr>
                                <?php } ?>
                                </tbody>
                            </table>
                            
                            <script type="text/javascript">
        function updateClick(id) {
            //var tr = document.getElementById(id).innerHTML;
            obj = document.getElementById(id);
            //var name = obj.getElementById("name").innerHTML; 
            //var sex = obj.getElementById("sex").innerHTML;
            //var duty = obj.getElementById("duty").innerHTML;
            var name = obj.cells[0].innerText;
            var sex = obj.cells[1].innerText;
            var duty = obj.cells[4].innerText;
            if(window.confirm('确定更新用户信息？')){
                var url = document.URL.substring(0, document.URL.length-1);
                url = url + "/" + id + "/" + name + "/" + sex + "/" + duty;
                window.location = url;
            }
            return;
        }

        function ShowElement(element) {
            var oldhtml = element.innerHTML;
            //如果已经双击过，内容已经存在input，不做任何操作
            if(oldhtml.indexOf('type="text"') > 0){
                return;
            }
            //创建新的input元素
            var newobj = document.createElement('input');
            //为新增元素添加类型
            newobj.type = 'text';
            newobj.style="width:100%";
            //为新增元素添加value值
            newobj.value = oldhtml;
            //为新增元素添加光标离开事件
            newobj.onblur = function() {
                element.innerHTML = this.value == oldhtml ? oldhtml : this.value;
                //当触发时判断新增元素值是否为空，为空则不修改，并返回原有值 
            }
            //设置该标签的子节点为空
            element.innerHTML = '';
            //添加该标签的子节点，input对象
            element.appendChild(newobj);
            //设置选择文本的内容或设置光标位置（两个参数：start,end；start为开始位置，end为结束位置；如果开始位置和结束位置相同则就是光标位置）
            newobj.setSelectionRange(0, oldhtml.length);
            //设置获得光标
            newobj.focus();
        }
    </script>
                            
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </section>
    <!--main content end-->
</section>
<?php include dirname(__FILE__) . '/footer.php' ?>
