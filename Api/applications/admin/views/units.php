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
                    <h1 class="h2">部门列表</h1>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th>部门Id</th>
                                    <th>部门名称</th>
                                    <th>创建时间</th>
                                    <th>删除操作</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                foreach ($units as $unit) {
                                    $url = "ajaxdelunit/".$unit['id'];
                                    ?>
                                    <tr <?php echo 'id="'.$unit['id'].'"' ?>>
                                        <td ><?=$unit['id']?></td>
                                        <td ><?=$unit['name']?></td>
                                        <td ><?=$unit['createtime']?></td>
                                        <td><a href="javascript:if(confirm('确认删除操作么?'))location='<?php echo site_url($url)?>'">删除</a></td> 
                                    </tr>
                                <?php } ?>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </section>
    <!--main content end-->
</section>
<?php include dirname(__FILE__) . '/footer.php' ?>

