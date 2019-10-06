var seckill = {
    // 封装秒杀相关ajax的URL
    URL: {
        now: function () {
            return '/seckills/time/now';
        },
        exposer: function (seckillId) {
            return '/seckills/' + seckillId + '/exposer';
        },
        execution: function (seckillId, md5) {
            return '/seckills/' + seckillId + '/' + md5 + '/execute';
        }
    },
    handlerSeckill: function (seckillId, node) {
        node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId), {}, function (res) {
            if (res && res.success) {
                var exposer = res.data;
                if (exposer && exposer.exposed) {
                    // 秒杀开始
                    // 获取秒杀地址
                    var md5 = exposer.md5;
                    var killUrl = seckill.URL.execution(seckillId, md5);
                    console.log(killUrl);
                    $('#killBtn').one('click', function () {
                        // 先禁用按钮
                        $(this).addClass('disabled');
                        $.post(killUrl, {}, function (res) {
                            if (res && res.success) {
                                var killResult = res.data;
                                var state = killResult.state;
                                var stateInfo = killResult.stateInfo;
                                node.html('<span class="label label-success">'+ stateInfo +'</span>')
                            }
                        })
                    });
                    node.show();
                } else {
                    // 秒杀未开始，重新开始计时
                    var now = exposer.now;
                    var startTime = exposer.startTime;
                    var endTime = exposer.endTime;
                    seckill.countDown(seckillId, now, startTime, endTime);
                }
            } else {
                console.log("res:" + res);
            }
        })
    },
    validatePhone: function (phone) {
        if (phone && phone.length == 11 && !isNaN(phone)) {
            return true;
        } else {
            return false;
        }
    },
    countDown: function (seckillId, nowTime, startTime, endTime) {
        var seckillBox = $('#seckill-box');
        // 时间判断
        if (nowTime > endTime) {
            seckillBox.html('秒杀结束');
        } else if (nowTime < startTime) {
            var killTime = new Date(startTime);
            seckillBox.countdown(killTime, function (event) {
                var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                seckill.handlerSeckill(seckillId,seckillBox);
            });
        } else {
            seckill.handlerSeckill(seckillId,seckillBox);
        }
    },
    // 详情页秒杀逻辑
    detail: {
        // 详情页初始化
        init: function (params) {
            //手机验证和登录，计时交互
            // 在cookie中查找手机号
            var killPhone = $.cookie('killPhone');
            var startTime = params.startTime;
            var endTime = params.endTime;
            var seckillId = params.seckillId;
            if (!seckill.validatePhone(killPhone)) {
                // 绑定phone
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show: true,
                    backdrop: 'static',
                    keyboard: false
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if (seckill.validatePhone(inputPhone)) {
                        // 将电话写入cookie
                        $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckills'});
                        // 刷新页面
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误！</label>').show(300);
                    }
                });
            }
            // 已经登录
            // 计时交互
            $.get(seckill.URL.now(), {}, function (res) {
                    if (res && res.success) {
                        var nowTime = res.data;
                        console.log('nowTime:' + nowTime);
                        seckill.countDown(seckillId, nowTime, startTime, endTime);
                    } else {
                        console.log(res);
                    }
                }
            )
        }
    }
};