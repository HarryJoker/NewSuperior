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
                    <h1 class="h2">添加Banner</h1>
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
                            <form name="userfile" enctype="multipart/form-data" class="form-horizontal form-border layui-form" action="newbanner" method="post" >
                                <div class="form-group">
                                    <label class="col-sm-3 control-label">展示标题</label>
                                    <div class="col-sm-6">
                                        <input type="text" class="form-control" name="title" id="input1" required="" placeholder="请填写标题">
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label class="col-sm-3 control-label">链接地址</label>
                                    <div class="col-sm-6">
                                        <input type="text" id="link" style="width:100%;overflow:auto" required="" name="link" id="input9" placeholder="请填写链接地址"></textarea>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-3 control-label">展示图片（可选）</label>
                                    <div class="col-sm-6">
                                        <input type="file" class="form-control" name="userfile" required="" placeholder="请填写标题">
                                    </div>
                                </div>

                                <div class="wrap">
                                    <div class="col-sm-6">
                                        <input id="btn" class="layui-btn" lay-submit lay-filter="formDemo" type="submit" style="height:50px;width:300px;"></input>
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

