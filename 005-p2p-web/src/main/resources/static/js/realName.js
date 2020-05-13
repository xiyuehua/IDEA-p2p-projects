//同意实名认证协议
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
        var phone = $("#phone").val();
        if ("" == phone) {
            showError("phone", "您输入您的手机号码~");
        } else if (!/^1[1-9]\d{9}$/.test(phone)) {
            showError("phone", "请输入正确的手机格式");
        } else {
            showSuccess("phone");
        }
    });

    //验证真实姓名
    $("#realName").on("blur", function () {
        var realName = $("#realName").val();
        if ("" == realName) {
            showError("realName", "请输入您的真实姓名");
        } else if (!/^[\u4e00-\u9fa5]{0,}$/.test(realName)) {
            showError("realName", "姓名只能输入中文哦~");
        } else {
            showSuccess("realName");
        }
    });
    //验证身份证号
    $("#idCard").on("blur", function () {
        var idCard = $("#idCard").val();
        if ("" == idCard) {
            showError("idCard", "请输入您的身份证号码~");
        }else if (!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)) {
            showError("idCard", "请输入正确的身份证格式");
        }else {
            showSuccess("idCard");
        }
    });
    //验证手机验证码
    $("#messageCode").on("blur",function () {
        var messageCode = $("#messageCode").val();
        if ("" == messageCode) {
            showError("messageCode", "请输入您的手机验证码");
        } else {
            showSuccess("messageCode");
        }
    });


    //点击获取验证码
    $("#messageCodeBtn").on("click", function () {
        //先让短信验证码的错误信息先清空
        hideError("messageCode");
        var phone = $("#phone").val();
        //先让密码框和手机号失去焦点进行验证

        $("#phone").blur();
        $("#realName").blur();
        $("#idCard").blur();

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

    //开始进行实名认证
    $("#btnRegist").on("click",function () {
       $("#phone").blur();
       $("#realName").blur();
       $("#idCard").blur();
       $("#messageCode").blur();

       var ErrTexts = $("div[id$='Err']").text();
        if (ErrTexts == '') {
            var phone = $("#phone").val();
            var messageCode = $("#messageCode").val();
            var idCard = $("#idCard").val();
            var realName = $("#realName").val();
            $.ajax({
                url: basePath + "/user/realName",
                type: "post",
                data: {
                    "phone":phone,
                    "realName":realName,
                    "idCard":idCard,
                    "messageCode":messageCode
                },
                success:function (data) {
                    if (data.success) {
                        window.location.href = basePath + "/loan/myCenter";
                    } else {
                        showError("messageCode",data.message)
                    }
                },
                error:function () {
                    showError("messageCode", "系统繁忙,请稍后重试");
                }
            });
        }

    });

});

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