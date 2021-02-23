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
                    <h1 class="h2">添加部门</h1>
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
                        <script type="text/javascript">
                                function check()
                                { 
                                    var checks = document.getElementsByName("parentIds[]");
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
                                        alert('请至少选择一个分管领导');
                                        return false;     
                                    }
                                }
                            </script>
                        <div class="panel-body">
                            <form name="userfile" enctype="multipart/form-data" class="form-horizontal form-border" id="form" action="newunit" method="post" onsubmit="return check()">
                                <div class="form-group">
                                    <label class="col-sm-3 control-label">分管领导</label>
                                        <div class="col-sm-6">
                                            <?php foreach($units as $unit) { ?>
                                            <div class="col-sm-3">
                                            <input type="checkbox" id="" name="parentIds[]" value="<?=$unit['id']?>"><?='   '.$unit['name']?> 
                                        
                                            </div>
                                            <? } ?>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">部门名称</label>
                                    <div class="col-sm-3">
                                        <input type="text" class="form-control" name="name" id="input1" required="" placeholder="请填写部门名称">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">部门头像（可选）</label>
                                    <div class="col-sm-6">
                                        <input type="file" class="form-control" name="userfile" placeholder="请填写标题">
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
