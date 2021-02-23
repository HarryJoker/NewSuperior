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
                    <h1 class="h2">已发任务列表</h1>
                </div>
            </div>
                            <form class="form-horizontal form-border" action="newtask" method="post" onsubmit="return check()">
                                <div class="form-group">
                                    <label required class="col-sm-3 control-label">任务分类</label>
                                    <div class="col-sm-6">
                                        <select name='category' required="" onchange="changeCategory(this.value)">
<?php foreach($categorys as $key=>$name){
?>
                                            <option value="<?=$key?>" <?php echo ($check == $key ? "selected" : "") ?> ><?=$name?></option>
<?php
}
?>
                                        </select>
                                    </div>
                                </div>
                            </form>
                                        <script>
                                                function changeCategory(value) {
                                                    document.forms[0].action = value;
                                                    document.forms[0].submit();
                                                }
										</script>
            <div class="row">
                <div class="col-md-12">
                    <div class="panel panel-default">
                        <div class="panel-body">
                            <table id="example" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                <tr>
                                    <th class="col-md-2">重点工作</th>
                                    <th class="col-md-6">具体项目</th>
                                    <th class="col-md-1">任务状态</th>
                                    <th class="col-md-1">责任单位</th>
                                    <th class="col-md_2">操作</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                foreach ($tasks as $task) {
                                    ?>
                                    <tr <?php echo 'id="'.$task['id'].'"' ?>>
                                        <td ><?=$task['name']?></td>
                                        <td 
											<font size="4" color="blue">
											<?php if ($category == 4) {
												echo '【'.$task['taskLabel'].'】';

											}?>
											</font>
											<?=$task['plan']?>
										</td>
                                        <td ><? echo $task['accept'] == 0 ? '未领取' : '已领取'?></td>
                                        <td ><?=$task['unitName']?></td>
                                        <td>
											<?php if ($task['progress'] < 5) {?>
											<a href="javascript:if(confirm('确认撤回操作么?'))location='<?php echo site_url("recalltask/".$task['id']."/".$check)?>'">撤回任务</a> &nbsp &nbsp
											<a href="javascript:if(confirm('确认调度完成操作么?'))location='<?php echo site_url("finishtask/".$task['id']."/".$check)?>'">调度完成</a>
											<?}?>
										</td> 
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

