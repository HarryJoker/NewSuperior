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
                    <h1 class="h2">发布通知</h1>
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
                            <form name="userfile" enctype="multipart/form-data" class="form-horizontal form-border" id="form" action="newnotification" method="post" onsubmit="return check()">

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">通知部门</label>
                                    <div class="col-sm-9">
                                        <?php foreach($unit_list as $unit) { ?>
                                       <div class="col-sm-3">
                                            <input type="checkbox" require id="" name="unitIds[]" value="<?=$unit['id']?>"><?='   '.$unit['name']?> 
                                        </div>
                                        <? } ?>
                            <!--
                                        <select name='unitid' required="">
                                              <option value="">请选择要通知的部门</option>
                                              <option value="-1">全部部门</option>
                                            <?php
                                                foreach($units as $unit)
                                                {

                                            ?>
                                                    <option value="<?=$unit['id']?>"><?=$unit['name']?></option>
                                            <?
                                                }
                                            ?>
                                        </select>
                                    -->
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">通知标题</label>
                                    <div class="col-sm-6">
                                        <input type="text" class="form-control" name="title" required="" placeholder="请填通知标题">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">通知内容</label>
                                    <div class="col-sm-6">
                                        <textarea rows="10" class="form-control" name="content" required="" placeholder="请填写通知内容"></textarea>
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

