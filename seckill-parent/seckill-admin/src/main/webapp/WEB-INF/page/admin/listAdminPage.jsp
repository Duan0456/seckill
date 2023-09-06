<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/commons/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <!-- jQuery -->
    <link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css">
    <link type="text/css" rel="stylesheet" href="${ctx}/static/css/style.css">
    <link type="text/css" rel="stylesheet" href="${ctx}/static/css/select.css"/>
    <link type="text/css" rel="stylesheet" href="${ctx}/static/css/adminframe.css"/>
    <script type="text/javascript" src="${ctx}/static/js/jquery-1.9.1.js"></script>
    <title>后台管理系统</title>
</head>
<body>

<div id="framecontentTop">
    <%@ include file="/commons/adminUser.jsp" %>
</div>
<div id="framecontentLeft">
    <jsp:include page="/commons/left.jsp"/>
</div>


<div id="maincontent">

    <div class="place">
        <span>位置：</span>
        <ul class="placeul">
            <li><a href="#">管理员列表</a></li>
        </ul>
    </div>
    <div class="rightinfo">
        <div class="formbody">
            <table width="90%"  class="tablelist">
                <thead>
                <tr align="center">
                    <th>登录名称</th>
                    <th>管理员名称</th>
                    <th>创建时间</th>
                    <th>登录范围</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${list}" var="record" varStatus="status">
                    <tr>
                        <td>${record.loginName}</td>
                        <td>${record.name}</td>
                        <td>${record.createTimeString}</td>
                        <td>
                            <c:choose>
                                <c:when test="${record.ipRange == null}">无限制</c:when>
                                <c:otherwise>
                                    限制区域登录
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="clear"></div>
</div>

<div class="clear"></div>
<script type="text/javascript" src="${ctx}/static/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jqPaginator.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/select-ui.min.js" defer="defer"></script>
<script type="text/javascript" src="${ctx}/static/DatePicker/WdatePicker.js"></script>
</body>
</html>
