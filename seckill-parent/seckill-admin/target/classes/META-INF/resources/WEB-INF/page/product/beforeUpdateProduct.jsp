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
            <li>-&gt;添加秒杀详情</li>
        </ul>
    </div>
    <div class="formbody">
        <div class="formtitle"><span>新增秒杀详细信息</span></div>
        <form action="${ctx}/product/doUpdateProduct" method="post" id="submitForm" name="submitForm">
            <ul class="forminfo">
                <c:if test="${errorMsg != null}">
                    <li><label>异常：</label><font color="red">${errorMsg}</font></li>
                </c:if>
                <li>
                    <label>秒杀名称：</label><input name="name" id="name" type="text" class="dfinput" value="${item.name}"/>
                    <input type="hidden" name="id" id="id" value="${item.id}"/>
                </li>
                <li><label>总库存数量：</label><input name="amount" id="amount"  type="text" class="dfinput" value="${item.count}"/></li>
                <li><label>秒杀开始时间：</label>
                    <input name="startBuyTimeStr" id="startBuyTimeStr"  type="text" class="Wdate" onclick="WdatePicker({skin:'whyGreen',dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'2020-03-08 11:30:00',maxDate:'2025-03-10 20:59:30'})" value="${item.startBuyTimeString}"/>
                </li>
                <li><label>商品简介：</label><input name="desc" id="desc" value="${item.productDesc}" type="text" class="dfinput"/>&nbsp;&nbsp;请输入商品简介及描述</li>
                <li><label>&nbsp;</label><input type="button" class="btn" value="确定修改" id="btn_sub" onclick="doFormBody()"/></li>
            </ul>
        </form>
    </div>

    <script type="application/javascript">
        function doFormBody() {
            if (checkFormBody()) document.submitForm.submit();
        }
        function checkFormBody() {
            var name = document.getElementById("name").value;
            if (name == ''|| name == null) {
                alert("秒杀名称不能为空");
                return false;
            }

            var amount = document.getElementById("amount").value;
            if (amount == null || amount == '') {
                alert("秒杀总库存不能为空");
                return false;
            } else {
                var regPos = /^\d+$/;
                if (!regPos.test(amount)) {
                    alert("秒杀总库存必须为数字");
                    return false;
                }
            }

            var startBuyTimeStr = document.getElementById("startBuyTimeStr").value;
            if (startBuyTimeStr == ''|| startBuyTimeStr == null) {
                alert("秒杀开始时间不能为空");
                return false;
            }

            return true;
        }
    </script>

    <div class="clear"></div>
</div>

<div class="clear"></div>
<script type="text/javascript" src="${ctx}/static/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/jqPaginator.min.js"></script>
<script type="text/javascript" src="${ctx}/static/js/select-ui.min.js" defer="defer"></script>
<script type="text/javascript" src="${ctx}/static/DatePicker/WdatePicker.js"></script>
</body>
</html>
