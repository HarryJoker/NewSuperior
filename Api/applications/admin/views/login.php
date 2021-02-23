<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <meta name="renderer" content="webkit|ie-comp|ie-stand">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width,initial-scale=1,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
    <meta http-equiv="Cache-Control" content="no-siteapp" />
    <title>博兴政务督查后台登录</title>
    <meta name="keywords" content="政务督查">
    <meta name="description" content="博兴政务督查">
	<link href="../css/login_style.css" rel='stylesheet' type='text/css' />
	<script src="../js/jquery-2.1.4.min.js" type="text/javascript"></script>
</head>
<body>

<div class="main">
		<div class="login">
			<h1>博兴政务督查后台管理系统</h1>
			<div class="inset">
				<!--start-main-->
				<form>
			         <div>
			         	<h2>管理登录</h2>
						<span><label>用户名</label></span>
						<span><input id="username" name="username" placeholder="请输入用户名" type="text" class="textbox" ></span>

					 </div>

					 <div>
						<span><label>密码</label></span>
					    <span><input id="password" name="password" type="password" placeholder="请输入密码" class="password"></span>
					 </div>

					<div class="sign">
                        <input id="btn" type="submit" value="登录" class="submit" />
					</div>
					</form>
				</div>
			</div>
		<!--//end-main-->
		</div>

<div class="copy-right">
	<p>&copy; 2017 Ethos Login Form. All Rights Reserved</p>
</div>

<script>
    $(function(){
        $(window).keydown(function(event){
            if(event.keyCode == 13){
                $('#btn').click();
            }
        });

        $('#btn').click(function(){
            var name = $('#username').val();
            if(name == "" || name == null){ $("#namek").show(150); return false;}
            else{ $("#namek").hide();}
            var pwd = $('#password').val();
            if(pwd == "" || pwd == null){ $('#pwdk').show(250); return false; }
            else{ $("#pwdk").hide();}
            //var ckt = $("#checkbox").is(':checked');
            //var ckv ;
            //if(ckt == true){ ckv = 0; }
            //else{ ckv = 1; }
            $.post("login",{ username : name, password: pwd },
                function(data){
                    if(data == 0){
                        window.location = 'main';
                    }else{
                        layer.alert(data);
                    }
                }
            );
        });
    });
</script>
</body>
</html>
