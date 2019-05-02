<%--
  Created by IntelliJ IDEA.
  User: xwd
  Date: 19-5-2
  Time: 上午10:57
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>秒杀详情页</title>
    <%@ include file="common/head.jsp" %>
</head>
<body>
<div class="container">
    <div class="panel panel-default text-center">
        <div class="panel-heading">${secKill.name}</div>
    </div>
    <div class="panel-body text-center">
        <h2 class="text-danger">
            <span class="glyphicon glyphicon-time"></span>
            <span class="glyphicon" id="seckill-box"></span>
        </h2>
    </div>
</div>

<div id="killPhoneModal" class="modal fate">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title text-center">
                    <span class="glyphicon glyphicon-phone"></span>
                </h3>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-xs-8 col-xs-offset-2">
                        <input type="text" name="killPhone" id="killPhoneKey" placeholder="请填手机号" class="form-control">
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <span id="killPhoneMessage" class="glyphicon"></span>
                <button type="button" id="killPhoneBtn" class="btn btn-success">
                    <span class="glyphicon glyphicon-phone"></span>
                </button>
            </div>
        </div>
    </div>

</div>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.staticfile.org/jquery/2.1.1/jquery.min.js"></script>

<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.staticfile.org/twitter-bootstrap/3.3.7/js/bootstrap.min.js"></script>


<!-- jQuery cookie 插件 www.bootcdn.cn -->
<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>

<!-- jQuery countDown 倒计时插件 -->
<script src="https://cdn.bootcss.com/jquery.countdown/2.2.0/jquery.countdown.min.js"></script>

<script src="/resources/script/seckill.js" type="text/javascript"></script>

<script type="text/javascript">
    $(function () {
        seckill.detail.init({
            seckillId:${secKill.seckillId},
            startTime:${secKill.startTime.time}, // 毫秒
            endTime:${secKill.endTime.time}
        });
    })
</script>
</body>
</html>
