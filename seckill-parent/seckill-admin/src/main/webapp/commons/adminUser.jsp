<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
</head>
<body style="background:url(${ctx}/static/images/topbg.gif) repeat-x;">
<div class="topleft">
  <a href="#" target="_parent"><img src="${ctx}/static/images/logo.png" title="系统首页" /></a>
</div>
<ul class="nav">
</ul>
<div class="topright">
  <ul>
   <li></li>
    <li><a href="${ctx}/user/logout" target="_parent">退出</a></li>
  </ul>
  <div class="user">
    <%--<span>${sessionScope.session_admin.nickName}</span>--%>
  </div>
</div>
<div style="clear: both"></div>
</body>
</html>
