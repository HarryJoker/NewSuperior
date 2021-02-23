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
                    <h1 class="h2">通知列表</h1>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th class="col-md-2">消息标题</th>
                                    <th class="col-md-6">消息内容</th>
                                    <th class="col-md-1">通知部门</th>
                                    <th class="col-md-1">是否已读</th>
                                    <th class="col-md_2">创建时间</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                foreach ($messages as $message) {
                                    ?>
                                    <tr <?php echo 'id="'.$message['id'].'"' ?>>
                                        <td><?=$message['title']?></td>
                                        <td>
											<?=$message['content']?>
										</td>
                                        <td><?=$message['unitName']?></td>
                                        <td>
											<?php echo $message['read'] == 0 ? '未读' : '已读' ?>
										</td> 
                                        <td ><?=$message['createtime']?></td>
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

