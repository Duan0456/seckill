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
            <li><a href="#">全部秒杀信息列表</a></li>
        </ul>
    </div>

    <div class="rightinfo">
        <div class="formbody">
            <form name="submitForm" id="submitForm" action="${ctx}/product/listPageSeckillProducts" method="post">
                <table width="90%">
                    <tbody>
                    <c:if test="${param.isSave == 'yes'}">
                        <tr>
                            <td colspan="2"><font color="blue">新增或修改秒杀信息保存成功</font>
                            </td>
                        </tr>
                    </c:if>
                    <tr>
                        <td width="50%">商品name:<input type="text" class="dfinput" name="name" id="name" value="${name}"/></td>
                        <td width="50%"  align="center">
                            <input type="button" class="btn"name="add" id="add" value="查询秒杀" onclick="doSearchForm();"/>&nbsp;&nbsp;
                            <input type="button" class="btn" name="search" id="search" value="新增秒杀详情"  onclick="addProcess();"/>&nbsp;&nbsp;
                        </td>
                    </tr>
                    <tr>
                        <td width="50%">&nbsp;</td>
                        <td width="50%">&nbsp;</td>
                    </tr>
                    </tbody>
                </table>
                <table width="90%"  class="tablelist">
                    <thead>
                    <tr align="center">
                        <th>商品名称</th>
                        <th>总库存</th>
                        <th>已售数量</th>
                        <th>创建时间</th>
                        <th>更新时间</th>
                        <th>商品开始销售时间</th>
                        <th>唯一标识</th>
                        <th>上架状态</th>
                        <th>删除状态</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${list}" var="record" varStatus="status">
                        <tr>
                            <td>${record.name}</td>
                            <td>${record.count}</td>
                            <td>${record.saled}</td>
                            <td>${record.createTimeString}</td>
                            <td>${record.updatedTimeString}</td>
                            <td>${record.startBuyTimeString}</td>
                            <td>${record.productPeriodKey}</td>
                            <td align="center">
                                <c:choose>
                                    <c:when test="${1 == record.status}">在线</c:when>
                                    <c:when test="${2 == record.status}">未上线</c:when>
                                    <c:when test="${3 == record.status}">已结束</c:when>
                                    <c:otherwise>
                                        状态未知
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${1 == record.isDeleted}">逻辑删除</c:when>
                                    <c:otherwise>正常</c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <a style="color:blue" href="${ctx}/product/showProductItem?id=${record.id}">查看</a>&nbsp;&nbsp;
                                <c:if test="${record.status == null || 2 == record.status}">
                                <a style="color:blue" href="${ctx}/product/beforeUpdateProduct?id=${record.id}">修改</a>&nbsp;&nbsp;
                                </c:if>
                                <c:if test="${record.status != null && 1 == record.status}">
                                    <a style="color:blue" href="${ctx}/product/updateProductStatus?id=${record.id}&status=2">下架</a>
                                </c:if>
                                <c:if test="${record.status != null && 2 == record.status}">
                                    <a style="color:blue" href="${ctx}/product/updateProductStatus?id=${record.id}&status=1">上架</a>
                                    &nbsp;&nbsp;
                                    <c:if test="${1 != record.isDeleted}"><a style="color:blue" href="${ctx}/product/deleteProduct?id=${record.id}">删除</a></c:if>
                                </c:if>
                            </td>
                        </tr>

                    </c:forEach>

                    <tr>
                        <td colspan="10" align="center">
                            <input type="hidden" name="pageNum" id="pageNum" value="${pageNum}"/>
                            <input type="hidden" name="pageSize" id="pageSize" value="${pageSize}"/>
                            <c:if test="${pageNum > 1 && totalPage > 1}">
                                <a href="#" onclick="sub(${pageNum-1});">上一页</a>
                            </c:if>
                            <c:if test="${pageNum == 1 || totalPage == 1}">
                                首页
                            </c:if>&nbsp;&nbsp;&nbsp;&nbsp;
                            <c:if test="${pageNum < totalPage}">
                                <a href="#" onclick="sub(${pageNum+1});">下一页</a>
                            </c:if>
                            <c:if test="${pageNum >= totalPage}">
                                末页
                            </c:if>&nbsp;&nbsp;&nbsp;&nbsp;
                            第${pageNum}页/共${totalPage}页&nbsp;每页${pageSize}条&nbsp;&nbsp;
                        </td></tr>
                    </tbody>

                </table>
            </form>
        </div>
    </div>

    <script type="text/javascript">
        function sub(pageNum) {
            document.getElementById("pageNum").value= pageNum;
            // $('#newContent').val(testEditor.getMarkdown());
            document.getElementById("submitForm").submit();
            //document.querySelectorAll("*[class*='isRightShow_-']");

        }
        function addProcess() {
            location.href="${ctx}/product/beforeCreateProduct";
        }
        function doSearchForm() {
            document.getElementById("submitForm").submit();
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
