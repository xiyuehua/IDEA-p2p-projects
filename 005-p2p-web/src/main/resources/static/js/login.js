var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
    try {
        if (window.opener) {
            // IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性
            referrer = window.opener.location.href;
        }
    } catch (e) {
    }
}

//按键盘Enter键即可登录
$(document).keyup(function (event) {
    if (event.keyCode == 13) {
        login();
    }
});


$(function () {

    //获取短信验证码
    $("#messageCodeBtn").on("click", function () {

        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        if ($("#messageCodeBtn").hasClass("on")) {
            return false;
        }

        if ("" == phone) {
            $("#showId").text("请输入您的手机号码");
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            $("#showId").text("手机号码格式有误");
        } else if ("" == loginPassword) {
            $("#showId").text("请输入您的密码");
        } else {
            //将错误消息清空
            $("#showId").html("");

            $.ajax({
                url: basePath + "/loan/getMessageCode",
                data: "phone=" + phone,
                type: "post",
                success: function (data) {
                    if (data.success) {
                        //通信成功 开始倒计时
                        alert("您的短信验证码是:" + data.data);
                        //通讯成功 开始倒计时
                        //如果没有倒计时的样式那么才开始倒计时 避免重复倒计时
                        //开始60s倒计时
                        $.leftTime(60, function (d) {
                            if (d.status) {
                                $("#messageCodeBtn").text((d.s == 00 ? 60 : d.s) + "s后重试");
                                $("#messageCodeBtn").addClass("on");
                            } else {
                                $("#messageCodeBtn").text("获取验证码");
                                $("#messageCodeBtn").removeClass("on");
                            }
                        });
                    } else {
                        $("#showId").text("获取验证码失败~");
                    }
                },
                error: function () {
                    $("#showId").text("系统繁忙,请稍后再试~");
                }
            });
        }
    });

    //登录
    $("#loginBtn").on("click", function () {
        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        var messageCode = $.trim($("#messageCode").val());
        if ("" == phone) {
            $("#showId").text("请输入您的手机号码");
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            $("#showId").text("手机号码格式有误");
        } else if ("" == loginPassword) {
            $("#showId").text("请输入您的密码");
        } else if (messageCode == "") {
            $("#showId").html("请输入验证码~");
        } else {
            //清空错误消息
            $("#showId").html("");
            $("#loginPassword").val($.md5(loginPassword));

            $.ajax({
                url: basePath + "/user/login",
                data:{
                    "phone":phone,
                    "loginPassword":$.md5(loginPassword),
                    "messageCode":messageCode
                },
                type: "post",
                success: function (data) {
                    if(data.success){
                        window.location.href = $("#targetPageUrl").val();
                    }else {
                        $("#showId").text(data.data);
                        $("#loginPassword").val("");
                        $("#messageCode").val("");
                    }
                },
                error:function () {
                    $("#loginPassword").val("");
                    $("#messageCode").val("");
                    $("#showId").text("系统繁忙,请稍后再试");
                }
            });
        }
    });
});
