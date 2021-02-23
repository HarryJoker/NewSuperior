<?php include dirname(__FILE__) . '/header.php' ?>
<body>
<section id="container">
    <!--sidebar end-->
    <?php include dirname(__FILE__) . '/left.php' ?>
    <!--main content start-->
    <section class="main-content-wrapper">
        <section id="main-content">
        <form id="myForm" class="form-horizontal form-border" action=<?php echo site_url("report").'/'.$category.'/0'?> method="post" >
                <div class="form-group ">
                    <label class="col-sm-4"><?php echo $title ?></label>
                    <label class="col-sm-2">选择导出日期</label>
                    <input class="col-sm-2" required="" id="time" name="exDate" type="month" value="<?=$exDate ?>"/>
                    <!-- <select class = "col-sm-2" name='category' required="" onchange="changeCategory(this.value)">
                        <option value="">请选择任务类型</option>
                    </select>
                    <label class="col-sm-1">
                        <a href=<?php echo site_url("export").'/'.$category ?>>导出</a>
                    </label>
                    -->
                    <label class="col-sm-1">
                        <a id="jsHref" href="javascript:;">导出</a>
                    </label>
                </div>
            </form>
                            <script type="text/javascript">
                                document.getElementById('jsHref').addEventListener('click', 
                                function(e) {
                                    e.preventDefault();
                                    document.getElementById("myForm").action = document.getElementById("myForm").action.replace(/report/, "export");
                                    document.getElementById("myForm").submit()
                                }, false);


                                document.getElementById('time').addEventListener('change', 
                                function(e) {
                                    e.preventDefault();
                                    document.getElementById("myForm").submit()
                                }, false);
                            </script>

            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th width="5%">序号</th>
                                    <th width="15%">重点工作</th>
                                    <th width="20%">年度目标任务</th>
<?php if ($category == 1) echo '<th width= 10%>推进计划</th>';?> 
<th width=<?$category == 1 ? "22%" : "32%"?>>进展情况</th>
                                    <th width="12%">责任人</th>
                                    <th width="8%">责任部门</th>
                                    <th width="8%">备注</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                $i = 1;
                                $progrezs = array('', '任务完成', '进展较快', '序时推进', '进度缓慢', '任务完成');
                                if (isset($tasks) && count($tasks))
                                {
                                    foreach ($tasks as $task) {
?>
                                    <tr>
                                        <td align="center"><?=$i++ ?></td>
                                        <td><?=$task['name']?></td>
                                        <td><?=$task['plan']?></td>
                                        <?php if ($category == 1) echo '<td>'.$task['planDetail'].'</td>'; ?> 
                                        <td><?=str_replace("\n", "<br/>", $task['content'])?></td>
                                        <td><?=str_replace("\n", "<br/>", $task['duumvir'])?></td>
                                        <td><?=$task['unitName']?></td>
                                        <td><?=$progrezs[$task['progress']]?></td>
                                    </tr>
                                <?php }
                                    }
                                 ?>
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

