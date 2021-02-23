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
                    <h1 class="h2">所有Banner</h1>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th>序号</th>
                                    <th>标题</th>
                                    <th>操作</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                foreach ($banners as $banner) {
                                    $url = "deletebanner/".$banner['id'];
                                    ?>
                                    <tr>
                                        <td><?=$banner['id']?></td>
                                        <td><?=$banner['title']?></td>
                                        <td><a href="javascript:if(confirm('确认要删除么?'))location='<?php echo site_url($url) ?>'">删除</a></td>

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

