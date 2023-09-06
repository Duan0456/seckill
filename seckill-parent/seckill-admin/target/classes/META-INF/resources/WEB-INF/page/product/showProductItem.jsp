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
            <li>-&gt;查看秒杀详情</li>
        </ul>
    </div>
    <div class="formbody">
        <div class="formtitle"><span>查看秒杀详细信息</span></div>

        <table width="90%">
            <tbody>
            <tr><td width="50%">秒杀id:</td><td width="50%"  align="left">${item.id}</td></tr>
            <tr>
                <td width="50%">秒杀name:</td>
                <td width="50%"  align="left">${item.name}</td>
            </tr>
            <tr><td width="50%">总库存数量:</td><td width="50%"  align="left">${item.count}</td></tr>
            <tr><td width="50%">已销售数量:</td><td width="50%"  align="left">${item.saled}</td></tr>
            <tr><td width="50%">创建时间:</td><td width="50%"  align="left">${item.createTimeString}</td></tr>
            <tr><td width="50%">更新时间:</td><td width="50%"  align="left">${item.updatedTimeString}</td></tr>
            <tr><td width="50%">是否删除:</td><td width="50%"  align="left">
                <c:choose>
                    <c:when test="${1 == item.isDeleted}">逻辑删除</c:when>
                    <c:otherwise>正常</c:otherwise>
                </c:choose>
            </td></tr>
            <tr><td width="50%">开始销售时间:</td><td width="50%"  align="left">${item.startBuyTimeString}</td></tr>
            <tr><td width="50%">秒杀商品介绍:</td><td width="50%"  align="left">${item.productDesc}</td></tr>
            <tr><td width="50%">商品状态:</td><td width="50%"  align="left">
                <c:choose>
                    <c:when test="${1 == item.status}">在线</c:when>
                    <c:when test="${2 == item.status}">未上线</c:when>
                    <c:when test="${3 == item.status}">已结束</c:when>
                    <c:otherwise>
                        状态未知
                    </c:otherwise>
                </c:choose>
            </td></tr>
            <tr><td width="50%">唯一标识key:</td><td width="50%"  align="left">${item.productPeriodKey}</td></tr>
            <tr><td colspan="2" align="center"><a style="color:blue" href="${ctx}/product/listPageSeckillProducts">返回列表页</a> </td> </tr>
            </tbody>
        </table>
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
