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
    $("#modifyPasswordBtn").on("click",function () {
        /*在隐藏域和文本框的值取出来*/
        var phone = $.trim($("#phone").val());
        var sessionPhone = $.trim($("#sessionPhone").val());
        var sessionPassword = $.trim($("#sessionPassword").val());
        var loginPassword = $.trim($("#loginPassword").val());
        var updatePassword = $.trim($("#updatePassword").val());
        var surePassword = $.trim($("#surePassword").val());
        var messageCode = $.trim($("#messageCode").val());

        if (!($.md5(loginPassword) == sessionPassword)) {
            //密码错误
            $("#showId").text("账号或者密码错误");
        } else {
            //原密码正确
            $("#showId").text("");
            if (phone == "") {
                $("#showId").text("请输入您的手机号码");
            }else if (!/^1[1-9]\d{9}$/.test(phone)) {
                $("#showId").text("手机号码格式有误");
            }else if (updatePassword == "") {
                $("#showId").text("请输入您的新密码");
            }else if (!/^[0-9a-zA-Z]+$/.test(updatePassword)) {
                $("#showId").text("新密码只能包含数字与字母哦~");
            }else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(updatePassword)) {
                $("#showId").text("新密码不能全英文或者全数字");
            }else if (updatePassword.length < 6 || updatePassword.length > 20) {
                $("#showId").text("新密码长度应在6-20之间");
            }else if (updatePassword == "") {
                $("#showId").text("请输入确认密码");
            }else if (!updatePassword == surePassword) {
                $("#showId").text("您俩次输入的密码不一致哦~");
            } else {
                if (phone == sessionPhone) {
                    //不用发短信验证码
                    $.ajax({
                        url:basePath +"/loan/modifyLoginPassword",
                        data:{
                            "phone":phone,
                            "updatePassword":$.md5(updatePassword)
                        },
                        type: "post",
                        success:function (data) {
                            if (data.success) {
                                window.location.href = basePath + "/loan/logout";
                            } else {
                                $("#showId").text(data.data);
                            }
                        },
                        error:function () {
                            $("#showId").text("系统繁忙,请稍后再试1");
                        }
                    });
                } else {
                    //发短息验证码
                    if (messageCode == "") {
                        $("#showId").text("请输入您的短信验证码~");
                    } else {
                        $.ajax({
                            url:basePath + "/loan/modifyLoginPassword",
                            data:{
                                "phone":phone,
                                "updatePassword":$.md5(updatePassword),
                                "messageCode":messageCode
                            },
                            type: "post",
                            success:function (data) {
                                if (data.success) {
                                    window.location.href = basePath + "/loan/logout";
                                } else {
                                    $("#showId").text(data.data);
                                }
                            },
                            error:function () {
                                $("#showId").text("系统繁忙,请稍后再试2");
                            }
                        });
                    }

                }
            }
        }
    });

    //获取短信验证码
    $("#messageCodeBtn").on("click", function () {
        var phone = $.trim($("#phone").val());
        var sessionPassword = $.trim($("#sessionPassword").val());
        var loginPassword = $.trim($("#loginPassword").val());
        var updatePassword = $.trim($("#updatePassword").val());
        var surePassword = $.trim($("#surePassword").val());

        var loginPassword = $.trim($("#loginPassword").val());
        if ("" == phone) {
            $("#showId").text("请输入您的手机号码");
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            $("#showId").text("手机号码格式有误");
        } else if ("" == loginPassword) {
            $("#showId").text("请输入您的密码");
        } else if (updatePassword == "") {
            $("#showId").text("请输入您的新密码");
        } else if (!/^[0-9a-zA-Z]+$/.test(updatePassword)) {
            $("#showId").text("新密码只能包含数字与字母哦~");
        }else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(updatePassword)) {
            $("#showId").text("新密码不能全英文或者全数字");
        }else if (updatePassword.length < 6 || updatePassword.length > 20) {
            $("#showId").text("新密码长度应在6-20之间");
        }else if (updatePassword == "") {
            $("#showId").text("请输入确认密码");
        } else if (!updatePassword == surePassword) {
            $("#showId").text("您俩次输入的密码不一致哦~");
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
});
