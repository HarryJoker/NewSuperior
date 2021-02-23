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
                    <h1 class="h2">任务列表</h1>
                </div>
            </div>
                            <form class="form-horizontal form-border" action="" method="post" onsubmit="return check()">
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
                                                    //document.forms[0].action = document.forms[0].action + '/' + value;
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
                                    <th width="20%">重点工作</th>
                                    <th>具体项目</th>
                                    <th width="10%">责任单位</th>
                                    <th width="10%">导出操作</th>
                                </tr>
                                </thead>

                                <tbody>
                                <?php
                                foreach ($tasks as $task) {
                                    ?>
                                    <tr <?php echo 'id="'.$task['id'].'"' ?>>
                                        <td ><?=$task['name']?></td>
                                        <td ><?=$task['plan']?></td>
                                        <td ><?=$task['unitId']?></td>
                                        <td></td> 
                                    </tr>
<?php
                                    foreach ($task['traces'] as $trace)
                                    $url = "exporttraceimg/".$trace['id'];
                                    { ?>
                                    <tr <?php echo 'id="'.$task['id'].'"' ?>>
                                        <td colspan=2><?=$trace['content']?></td>
                                        <td >
                                        
                                                <?php 
                                                $imgs = explode(',',$trace['attachment']); 
                                                foreach ($imgs as $img) 
                                                {
                                                    $imgUrl = base_url().'uploads/'.$img;
                                                ?>
                                                <img src=<?=$imgUrl?> width="30px" height="30px" />
                                                <?php } ?>
                                        </td>
                                        <td align="center"><a href="javascript:if(confirm('确认导出操作'))location='<?php echo site_url($url)?>'">导出</a></td> 
                                    </tr>

<?php } ?>
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

