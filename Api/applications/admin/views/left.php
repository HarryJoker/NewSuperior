<header id="header">
    <!--logo start-->
    <div class="brand">
        <a href='' class="logo"><span>博兴政务</a>
    </div>
    <!--logo end-->
    <div class="toggle-navigation toggle-left">
        <button type="button" class="btn btn-default" id="toggle-left" data-toggle="tooltip" data-placement="right"
                title="Toggle Navigation">
            <i class="fa fa-bars"></i>
        </button>
    </div>
</header>
<!--sidebar start-->
<aside class="sidebar">
    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'task'?'active':''?>">
                <a href="javascript:void(0);"><i class="fa fa-table"></i><span>任务管理</span><i
                            class="arrow fa fa-angle-right pull-right"></i></a>
                <ul>
                    <li class="<?=$activetwolevel == 'task'?'active':''?>"> <a href=<?php echo site_url("newtask") ?>>&nbsp&nbsp&nbsp&nbsp发布任务</a></li>
                    <li class="<?=$activetwolevel == 'tasks'?'active':''?>"> <a href=<?php echo site_url("tasks/1") ?>>&nbsp&nbsp&nbsp&nbsp已发任务</a></li>
                    <li class="<?=$activetwolevel == 'image'?'active':''?>"> <a href=<?php echo site_url("tasktrace") ?>>&nbsp&nbsp&nbsp&nbsp导出图片</a></li>
                </ul>
            </li>
        </ul>
    </div>

    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'user'?'active':''?> ">
                <a href="javascript:void(0);"><i class="fa fa-table"></i><span>账号管理</span><i
                            class="arrow fa fa-angle-right pull-right"></i></a>
                <ul>
                <li class="<?=$activetwolevel == 'verify'?'active':''?>"><a href=<?php echo site_url("verifyusers") ?>>&nbsp&nbsp&nbsp&nbsp账号审核</a>
                    </li>
                    <li class="<?=$activetwolevel == 'unenable'?'active':''?>"><a href= <?php echo site_url("enableusers") ?>>&nbsp&nbsp&nbsp&nbsp账号回收</a>
                    </li>
                    <li class="<?=$activetwolevel == 'edit'?'active':''?>"><a href= <?php echo site_url("editusers") ?>>&nbsp&nbsp&nbsp&nbsp账号修改</a>
                    </li>
                </ul>
            </li>
        </ul>
    </div>
    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'unit'?'active':''?> ">
                <a href="javascript:void(0);"><i class="fa fa-table"></i><span>部门管理</span><i
                            class="arrow fa fa-angle-right pull-right"></i></a>
                <ul>
                    <li class="<?=$activetwolevel == 'add'?'active':''?>"><a href=<?php echo site_url( "newunit") ?>>&nbsp&nbsp&nbsp&nbsp添加部门</a></li>
                    <li class="<?=$activetwolevel == 'all'?'active':''?>"><a href=<?php echo site_url( "allunits") ?>>&nbsp&nbsp&nbsp&nbsp删除部门</a></li>
                    <!--
                    <li class="4"><a href="showdeleteuser">&nbsp&nbsp&nbsp&nbsp删除部门</a></li>
                    -->
                </ul>
            </li>
        </ul>
    </div>

    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'banner'?'active':''?>">
                <a href="javascript:void(0);"><i class="fa fa-table"></i><span>Banner管理</span><i
                            class="arrow fa fa-angle-right pull-right"></i></a>
                <ul>
                    <li class="<?=$activetwolevel == 'add'?'active':''?>"> <a href=<?php echo site_url("newbanner") ?>>&nbsp&nbsp&nbsp&nbsp添加Banner</a></li>
                    <li class="<?=$activetwolevel == 'delete'?'active':''?>"> <a href=<?php echo site_url("banners") ?>>&nbsp&nbsp&nbsp&nbsp删除Banner</a></li>
                </ul>
            </li>
        </ul>
    </div>

    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'notification'?'active':''?> ">
                <a href="javascript:void(0);"><i class="fa fa-table"></i><span>通知管理</span><i
                            class="arrow fa fa-angle-right pull-right"></i></a>
                <ul>
                <li class="<?=$activetwolevel == 'new'?'active':''?>"><a href=<?php echo site_url("newnotification") ?>>&nbsp&nbsp&nbsp&nbsp发布通知</a></li>
                <li class="<?=$activetwolevel == 'all'?'active':''?>"><a href=<?php echo site_url("messages") ?>>&nbsp&nbsp&nbsp&nbsp通知列表</a></li>
                </ul>
            </li>
        </ul>
    </div>

    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'report'?'active':''?> ">
                <!--<a href=<?php echo site_url("report") ?>> -->
                <a href="javascript:void(0);">
                    <i class="fa fa-table"></i>
                    <span>导出报告</span>
                    <i class="arrow fa fa-angle-right pull-right"></i>
                </a>
                <ul>
                    <li class="<?=$activetwolevel == '1'?'active':''?>"> <a href=<?php echo site_url("report")."/1/0" ?>>&nbsp&nbsp&nbsp&nbsp政府工作报告</a></li>
                    <li class="<?=$activetwolevel == '2'?'active':''?>"> <a href=<?php echo site_url("report")."/2/0" ?>>&nbsp&nbsp&nbsp&nbsp市委市政务重大决策部署</a></li>
                    <li class="<?=$activetwolevel == '3'?'active':''?>"> <a href=<?php echo site_url("report")."/3/0" ?>>&nbsp&nbsp&nbsp&nbsp建议提案</a></li>
                    <li class="<?=$activetwolevel == '4'?'active':''?>"> <a href=<?php echo site_url("reportmeeting")."/4/1" ?>>&nbsp&nbsp&nbsp&nbsp会议议定事项</a>
                        <ul style="margin-left:20px;">
                            <li class="<?=isset($activethreelevel) && $activethreelevel == '1'?'active':''?>"> <a href=<?php echo site_url("reportmeeting")."/4/1" ?>>&nbsp&nbsp&nbsp&nbsp全体会议</a></li>
                            <li class="<?=isset($activethreelevel) && $activethreelevel == '2'?'active':''?>"> <a href=<?php echo site_url("reportmeeting")."/4/2" ?>>&nbsp&nbsp&nbsp&nbsp常务会议</a></li>
                            <li class="<?=isset($activethreelevel) && $activethreelevel == '3'?'active':''?>"> <a href=<?php echo site_url("reportmeeting")."/4/3" ?>>&nbsp&nbsp&nbsp&nbsp县长办公室会议</a></li>
                            <li class="<?=isset($activethreelevel) && $activethreelevel == '4'?'active':''?>"> <a href=<?php echo site_url("reportmeeting")."/4/4" ?>>&nbsp&nbsp&nbsp&nbsp专题会议</a></li>
                        </ul>
                    </li>
                    <li class="<?=$activetwolevel == '5'?'active':''?>"> <a href=<?php echo site_url("report")."/5/0" ?>>&nbsp&nbsp&nbsp&nbsp领导批示</a></li>
                    <li class="<?=$activetwolevel == '6'?'active':''?>"> <a href=<?php echo site_url("report")."/6/0" ?>>&nbsp&nbsp&nbsp&nbsp专项督查</a></li>
                    <li class="<?=$activetwolevel == '7'?'active':''?>"> <a href=<?php echo site_url("report")."/7/0" ?>>&nbsp&nbsp&nbsp&nbsp重点项目</a></li>
                </ul>
            </li>
        </ul>
    </div>


    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'msg'?'active':''?>">
                <a href="javascript:void(0);"><i class="fa fa-table"></i><span>短信管理</span><i
                            class="arrow fa fa-angle-right pull-right"></i></a>
                <ul>
                    <li class="<?=$activetwolevel == 'new'?'active':''?>"> <a href=<?php echo site_url("sendmsg") ?>>&nbsp&nbsp&nbsp&nbsp发送短信</a></li>
                </ul>
            </li>
        </ul>
    </div>
    
    <div id="leftside-navigation" class="nano">
        <ul class="nano-content">
            <li class="sub-menu <?=$activeonelevel == 'version'?'active':''?> ">
                <a href=<?php echo site_url("version") ?>>
                    <i class="fa fa-table"></i>
                    <span>发布版本</span>
                    <i class="arrow fa fa-angle-right pull-right"></i>
                </a>
            </li>
        </ul>
    </div>
</aside>

<!--sidebar end-->
