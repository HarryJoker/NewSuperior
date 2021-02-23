<?php include dirname(__FILE__) . '/header.php' ?>
<body>
<section id="container">
    <!--sidebar end-->
    <?php include dirname(__FILE__) . '/left.php' ?>
    <!--main content start-->
    <section class="main-content-wrapper">
        <section id="main-content">
        <form id="myForm" class="form-horizontal form-border" action=<?php echo site_url("reportmeeting").'/'.$category.'/'.$childType ?> method="post" >
                <div class="form-group ">
                    <label class="col-sm-4"><?php echo $title ?></label>
                    <select class = "col-sm-2" onchange="changeTaskLabel(this.value)" name="taskLabel" required="" >
                        <option value="">请选择任务标签</option>
                        <?php foreach($taskLabels as $label) { ?>
                            <option <?=$curTaskLabel==$label['taskLabel'] ? 'selected' : '' ?> value=<?=$label['taskLabel']?>><?=$label['taskLabel']?></option>
                        <?php } ?>
                    </select>
                    <label class="col-sm-1">
                        <a id="jsHref" href="javascript:;">导出</a>
                    </label>
                    <!--
                    <input type="submit" value="导出" style="color:#1abc9c;border:none;background-color:transparent"></input>
                    -->
                </div>
            </form>
                            <script type="text/javascript">
                                function changeTaskLabel(value)
                                {
                                    document.getElementById("myForm").submit()
                                }

                                document.getElementById('jsHref').addEventListener('click', 
                                function(e) {
                                    e.preventDefault();
                                    document.getElementById("myForm").action = document.getElementById("myForm").action.replace(/reportmeeting/, "export");
                                    document.getElementById("myForm").submit()
                                }, false);


//                                document.getElementById('taskLabels').addEventListener('onchange', 
//                                function(e) {
//                                    e.preventDefault();
//                                    document.getElementById("myForm").action = document.getElementById("myForm").action.replace(/export/, "report");
//                                    alert(xxx);
//                                    document.getElementById("myForm").submit()
//                                }, false);
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
                                    <th width="32%">进展情况</th>
                                    <th width="12%">责任人</th>
                                    <th width="8%">责任部门</th>
                                    <th width="8%">备注</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                $i = 1;
                                $progrezs = array('', '任务完成', '进展较快', '序时推进', '进度缓慢');
                                if (isset($tasks) && count($tasks))
                                {
                                    foreach ($tasks as $task) {
?>
                                    <tr>
                                        <td align="center"><?=$i++ ?></td>
                                        <td><?=$task['name']?></td>
                                        <td><?=$task['plan']?></td>
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


