//错误提示
function showError(id, msg) {
    $("#" + id + "Ok").hide();
    $("#" + id + "Err").html("<i></i><p>" + msg + "</p>");
    $("#" + id + "Err").show();
    $("#" + id).addClass("input-red");
}

//错误隐藏
function hideError(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id).removeClass("input-red");
}

//显示成功
function showSuccess(id) {
    $("#" + id + "Err").hide();
    $("#" + id + "Err").html("");
    $("#" + id + "Ok").show();
    $("#" + id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid, bosid) {
    $("#" + maskid).show();
    $("#" + bosid).show();
}

//关闭注册协议弹层
function closeBox(maskid, bosid) {
    $("#" + maskid).hide();
    $("#" + bosid).hide();
}

//注册协议确认
$(function () {
    $("#agree").click(function () {
        var ischeck = document.getElementById("agree").checked;
        if (ischeck) {
            $("#btnRegist").attr("disabled", false);
            $("#btnRegist").removeClass("fail");
        } else {
            $("#btnRegist").attr("disabled", "disabled");
            $("#btnRegist").addClass("fail");
        }
    });

    //验证手机号
    $("#phone").on("blur", function () {
        var phone = $.trim($("#phone").val());
        if ("" == phone) {
            showError("phone", "请输入您的手机号码");
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            showError("phone", "请输入正确的手机号码格式");
        } else {
            $.ajax({
                url: basePath + "/loan/checkPhone",
                data: "phone=" + phone,
                type: "get",
                success: function (data) {
                    if (data.success) {
                        showSuccess("phone");
                    } else {
                        showError("phone", data.message);
                    }
                },
                error: function () {
                    showError("系统繁忙,请稍后再试")
                }

            });
        }
    });

    //验证密码
    $("#loginPassword").on("blur", function () {
        var password = $.trim($("#loginPassword").val());
        if ("" == password) {
            showError("loginPassword", "请输入您的密码");
        } else if (!/^[0-9a-zA-Z]+$/.test(password)) {
            showError("loginPassword", "密码只能包含数字与字母哦~");
        } else if (!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(password)) {
            showError("loginPassword", "密码不能全英文或者全数字");
        } else if (password.length < 6 || password.length > 20) {
            showError("loginPassword", "密码长度应在6-20之间");
        } else {
            showSuccess("loginPassword");
        }
    });

    //验证验证码
    $("#messageCode").on("blur", function () {
        var messageCode = $.trim($("#messageCode").val());
        if ("" == messageCode) {
            showError("messageCode", "请输入短信验证码");
        } else {
            showSuccess("messageCode");
        }
    });

    //注册
    $("#btnRegist").on("click", function () {

        $("#phone").blur();
        $("#loginPassword").blur();
        $("#messageCode").blur();

        var phone = $.trim($("#phone").val());
        var loginPassword = $.trim($("#loginPassword").val());
        var messageCode = $.trim($("#messageCode").val());

        var ErrText = $("div[id$='Err']").text();
        if (ErrText == "") {
            $("#loginPassword").val($.md5(loginPassword))
            $.ajax({
                url: basePath + "/user/register",
                data: {
                    "phone": phone,
                    "password": $.md5(loginPassword),
                    "messageCode": messageCode
                },
                type: "post",
                success: function (data) {
                    if (data.success) {
                        window.location.href = basePath + "/page/realName";
                    } else {
                        showError("messageCode", data.message);
                        $("#loginPassword").val("");
                    }
                },
                error: function () {
                    showError("messageCode", "系统繁忙,请稍后重试");
                    $("#loginPassword").val("");
                }
            });
        }
    });

    //点击获取验证码
    $("#messageCodeBtn").on("click", function () {
        //然短信验证码的错误信息先清空
        hideError("messageCode");
        var phone = $("#phone").val();
        //先让密码框和手机号失去焦点进行验证

        $("#phone").blur();
        $("#loginPassword").blur();

        var ErrorTests = $("div[id $='Err']").text();
        //如果手机号和密码验证正确 那么开始发送验证码
        if (ErrorTests == "") {
            if (!$("#messageCodeBtn").hasClass("on")) {
                $.ajax({
                    url: basePath + "/loan/getMessageCode",
                    data: {
                        "phone": phone
                    },
                    type: "post",
                    success: function (data) {
                        if (data.success) {
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
                        }
                    },
                    error: function () {
                        showError("messageCodeErr", "系统繁忙,请稍后重试。")
                    }
                });
            }
        }
    });
});
