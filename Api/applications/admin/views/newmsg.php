<?php include dirname(__FILE__).'/header.php' ?>
<style>
    .wrap{
        margin:0px 300px auto;
        text-align:center;
    }

</style>
<body>
<section id="container">
    <!--sidebar end-->
    <?php include dirname(__FILE__).'/left.php' ?>

    <!--main content start-->
    <section class="main-content-wrapper">
        <section id="main-content">
            <div class="row">
                <div class="col-md-12">
                    <h1 class="h2">发送短信</h1>
                </div>
            </div>
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
									var phoneReg = /(^1[3|4|5|7|8]\d{9}$)|(^09\d{8}$)/;	
									var text = document.getElementById('phone').value;
									var phoneStr = text.replace(/，/g, ',');
									var phones = phoneStr.split(",");
									for (var i=0;i<phones.length;i++)
									{
										if (!phoneReg.test(phones[i])) {		
											alert('第'+(i+1)+'个手机号码无效！');		
											return false;	
										}
									}
                                }
                            </script>
                            <form name="userfile" enctype="multipart/form-data" class="form-horizontal form-border" id="form" method="post" onsubmit="return check()">

                                <div class="form-group">
                                    <label required class="col-sm-3 control-label">任务类别</label>
                                    <div class="col-sm-6">
                                        <select class="col-sm-3" name='category' required="">
											<option value="1">政府工作报告</option>
											<option value="2">会议议定事项</option>
											<option value="3">领导批示件</option>
                                        </select>
                                    </div>
                                </div>
								
                                <div class="form-group">
                                    <label class="col-sm-3 control-label">发送目标</label>
                                    <div class="col-sm-6">
                                        <select class="col-sm-3" name='whoId' required="">
											<option value="0">部门单位</option>
											<option value="1">分管领导</option>
                                        </select>
                                    </div>
                                </div>


                                <div class="form-group">
                                    <label class="col-sm-3 control-label">手机号码</label>
                                    <div class="col-sm-6">
                                        <input id="phone" type="text" class="form-control" name="phone" required="" placeholder="请填写手机号码,可用逗号分隔如（13436730367，13436730368）">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">任务标题</label>
                                    <div class="col-sm-6">
                                        <textarea id="content" rows="5" class="form-control" name="task" required="" placeholder="请填写任务标题(不超过100字)"></textarea>
                                    </div>
                                </div>


                                <div class="wrap">
                                    <div class="col-sm-6">
                                        <input type="submit" style="height:50px;width:300px;"></input>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

        </section>
    </section>
    <!--main content end-->
</section>
<?php include dirname(__FILE__).'/footer.php' ?>

