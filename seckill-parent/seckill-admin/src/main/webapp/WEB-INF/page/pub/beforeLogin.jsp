<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <title>欢迎登录后台管理系统</title>
    <link href="${ctx}/static/css/style.css" rel="stylesheet" type="text/css"/>
    <script language="JavaScript" src="${ctx}/static/js/jquery-1.9.1.js"></script>
    <script language="javascript">
        $(function () {
            $('.loginbox').css({'position': 'absolute', 'left': ($(window).width() - 692) / 2});
            $(window).resize(function () {
                $('.loginbox').css({'position': 'absolute', 'left': ($(window).width() - 692) / 2});
            })
        });
    </script>
</head>
<body style="background-color:#1c77ac; background-image:url(${ctx}/static/images/light.png); background-repeat:no-repeat; background-position:center top; overflow:hidden;">
<div id="mainBody">
    <div id="cloud1" class="cloud"></div>
    <div id="cloud2" class="cloud"></div>
</div>
<div class="logintop">
    <span>欢迎登录后台管理界面平台</span>

</div>
<div class="loginbody">
    <span class="systemlogo"></span>
    <div class="loginbox">
        <form action="${ctx}/user/loginPage" method="post">
            <ul>
                <li><input name="username" id="username" type="text" class="loginuser"/></li>
                <li><input name="password" type="password" class="loginpwd"/></li>
                <li><input type="submit" class="loginbtn" value="登录"/><label>
                    <span style="margin-top: 20px;color: red">
                        <c:if test="${errorCode=='10001'}">
                            用户名密码不能为空
                        </c:if>
                        <c:if test="${errorCode=='10002'}">
                            请输入正确的用户名和密码
                        </c:if>
                    </span>
                </label>
            </ul>
        </form>
    </div>
</div>
</body>
</html>
