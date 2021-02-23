<?php include dirname(__FILE__).'/header.php' ?> <style> .wrap{ margin:0px 300px auto; text-align:center; } </style> <body> <section id="container"> <!--sidebar end--> <?php include dirname(__FILE__).'/left.php' ?> <!--main content start--> <section class="main-content-wrapper"> <section id="main-content"> <div class="row"> <div class="col-md-12"> <h1 class="h2">发布任务</h1> </div> </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-heading">
                            <div class="actions pull-right">
                                <i class="fa fa-chevron-down"></i>
                                <i class="fa fa-times"></i>
                            </div>
                        </div>
                        <div class="panel-body">
                            <script type="text/javascript">
                                function check()
                                { 
                                    var checks = document.getElementsByName("unitIds[]");
                                    var checkedCount=0;
                                    for(i=0;i<checks.length;i++){
                                            if(checks[i].checked) checkedCount++;
                                    }
                                    if (checkedCount > 0)
                                    {
                                        return true;
                                    }
                                    else
                                    {
                                        alert('请至少选择一个责任部门');
                                        return false;     
                                    }
                                }
                            </script>
                            <form class="form-horizontal form-border" action="newtask" enctype="multipart/form-data" method="post" onsubmit="return check()">
                                <div class="form-group">
                                    <label required class="col-sm-2 control-label">任务分类</label>
                                    <div class="col-sm-6">
                                        <select name='category' required="" onchange="changeCategory(this.value)">
                                        <option value="">请选择任务类型</option>
                                        <?php foreach($category as $key=>$name){
                                            ?>
                                            <option value="<?=$key?>"><?=$name?></option>
                                            <?php
                                        }
                                        ?>
                                        </select>
                                    </div>
                                </div>

                                <div id="order" class="form-group">
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-2 control-label">责任单位</label>
                                    <div class="col-sm-9">
                                        <?php foreach($unit_list as $unit) { ?>
                                       <div class="col-sm-3">
                                            <input type="checkbox" id="" name="unitIds[]" value="<?=$unit['id']?>"><?='   '.$unit['name']?> 
                                        </div>
                                        <? } ?>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-sm-2 control-label">重点工作</label>
                                    <div class="col-sm-6">
                                        <input type="text" class="form-control" name="taskname" required="" placeholder="请填写重点工作">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-sm-2 control-label">具体项目</label>
                                    <div class="col-sm-6">
                                        <textarea rows="5" style="width:100%;overflow:auto" required="" name="plan" placeholder="请填写具体项目"></textarea>
                                    </div>
                                </div>

                                
                                <div id="plan" class="form-group">
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-2 control-label">提报日期</label>
                                    <div class="col-sm-6">
                                        <select required="" name='reportType' onchange="changeValue(this.value)">
                                            <option value="">请选择报送时间的类别</option>
                                            <option value="3">每周一次</option>
                                            <option value="1">每月一次</option>
                                            <option value="2">具体时间</option>
                                        </select>
                                        

                                        <input required="" style="width:200" placeholder="请填写报送周期日" id="time" name="reportDate" type="number" max="28" min="1" value=""/>
                                        
                                        <script>
                                                function changeValue(value){
                                                    var child = document.getElementById("time");
                                                    var reportTime = document.getElementById("reportTime");
                                                    if (reportTime != null)
                                                    {
                                                        reportTime.remove(0);

                                                    }
                                                    
                                                    if (value == 1)
                                                    {
                                                    	child.outerHTML = '<input required="" id="time" style="width:200" type="number" name="reportDate" placeholder="请填写报送周期日" max="28" min="1" value="" /> <input id="reportTime" required="" name="reportTime" type="time" />';
                                                    } 
                                                    else if (value == 2)
                                                    {
                                                    	child.outerHTML = '<input required="" id="time" type="datetime-local"  name="reportDate" value="" /> ';
                                                    } 
                                                    else if (value == 3)
                                                    {
                                                    	child.outerHTML = '<input required="" id="time" type="number" style="width:200" name="reportDate" max="7" min="1" value="" /> <input id="reportTime" required="" name="reportTime" type="time" />';
                                                    } 
                                                }

                                                function changeCategory(value) {
                                                    var plan = document.getElementById("plan");
                                                    var order = document.getElementById("order");
                                                    if (value == 1)
                                                    {
                                                        plan.innerHTML='<label class="col-sm-2 control-label">推进计划</label><div class="col-sm-6"><textarea rows="5" style="width:100%;overflow:auto" required="" name="planDetail" placeholder="请填写具体项目"></textarea></div>';
                                                    } 
                                                    else 
                                                    {
                                                        plan.innerHTML='';
                                                    }
                                                    if (value == 2)
                                                    {
                                                        order.innerHTML='<label required class="col-sm-2 control-label">选择排名</label><div class="col-sm-6"><select name="sequence" required="" ><option value="">请选择排名</option><option value="1">第一名</option><option value="2">第二名</option><option value="3">第三名</option><option value="4">第四名</option><option value="5">第五名</option><option value="6">第六名</option><option value="7">第七名</option></select></div>';
                                                    }
                                                    else if (value == 3)
                                                    {
                                                        order.innerHTML='<label required class="col-sm-2 control-label">任务种类</label><div class="col-sm-6"><select name="childType" required="" ><option value="">请选择任务种类</option><option value="1">人大</option><option value="2">政协</option>';
                                                    }
                                                    else if (value == 4)
                                                    {
                                                        order.innerHTML= '<label required class="col-sm-2 control-label">任务种类</label><div class="col-sm-7"><select name="childType" required="" ><option value="">请选择任务种类</option><option value="1">全体会议</option><option value="2">常务会议</option><option value="3">县长办公室会议</option><option value="4">专题会议</option></select>&nbsp&nbsp<input type="text" name="taskLabel" required="" placeholder="请填写任务标签"></div>';

                                                    }
                                                    else
                                                    {
                                                        order.innerHTML='';
                                                    }
                                                }
										</script>
                                        
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-2 control-label">优先级</label>
                                    <div class="col-sm-6 layui-input-block">
                                        <label><input name="priority" type="radio" value="3" />优先 </label>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
                                        <label><input name="priority" type="radio" checked  value="2" />次要 </label>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
                                        <label><input name="priority" type="radio" value="1" />最低 </label>&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-sm-2 control-label">附件照片</label>
                                    <div class="col-sm-9 layui-input-block">
                                        <div class="col-sm-2">
                                            <img id="preview" />
                                            <input class="" accept="image/png,image/jpeg"  type="file" name="userfile1" onchange="imgPreview(this)"/>
                                        </div>
                                    </div>
                                    </div>
                                </div>

                                <div class="wrap">
                                    <div class="col-sm-6">
                                        <br><br>
                                        <input type="submit" style="height:70px;width:500px;"></input>
                                        <br><br><br><br><br>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

	<script type="text/javascript">
    function imgPreview(fileDom){
        fileDom.style.display = "none";
        var container = fileDom.parentNode.parentNode;
        var img = fileDom.parentNode.getElementsByTagName("img")[0];
        //判断是否支持FileReader
        if (window.FileReader) {
            var reader = new FileReader();
        } else {
            alert("您的设备不支持图片预览功能，如需该功能请升级您的设备！");
        }

        //获取文件
        var file = fileDom.files[0];
        var imageType = /^image\//;
        //是否是图片
        if (!imageType.test(file.type)) {
            alert("请选择图片！");
            return;
        }
        //读取完成
        reader.onload = function(e) {
            //获取图片dom
            //var img = document.getElementById("preview");
            //图片路径设置为读取的图片
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);

        var imgCount = container.children.length;
        if(imgCount < 5) {
            var div = document.createElement("div");
            div.setAttribute("class", "col-sm-2");
            div.innerHTML = '<img id="preview" /><input type="file" accept="image/png,image/jpeg" name="userfile'+(imgCount + 1)+'" onchange="imgPreview(this)"/>';
            container.appendChild(div);
        }
    }
</script>

        </section>
    </section>
    <!--main content end-->
</section>

<?php include dirname(__FILE__).'/footer.php' ?>
