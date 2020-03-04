Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


$(function() {

    $("#btnSubmit").click(function(event) {
        event.preventDefault();
        $("#responseBody").empty().append('Loading...');
        let ajaxTime = new Date().getTime();
        $.ajax({
            type : "POST",
            dataType : "json",
            url : $("#apiForm").attr("action"),
            data : $("#apiForm").serialize(),
            success : function(data, textStatus, xhr) {
                let totalTime = (new Date().getTime() - ajaxTime) / 1000;
                let httpStatus = 502;
                if (xhr.status){
                    httpStatus = xhr.status;
                }
                let apiRequestInfo = {
                    "exe_time" : totalTime,
                    'http_status' : httpStatus,
                    'text_status': textStatus
                };
                jsonview(apiRequestInfo, $("#apiRequest"));
                jsonview(data, $("#responseBody"));
            },
            error : function(data, textStatus, xhr) {
                let totalTime = (new Date().getTime() - ajaxTime) / 1000;
                let httpStatus = 502;
                if (xhr.status){
                    httpStatus = xhr.status;
                }
                let apiRequestInfo = {
                    "exe_time" : totalTime,
                    'http_status' : httpStatus,
                    'text_status': textStatus
                };
                jsonview(apiRequestInfo, $("#apiRequest"));
                $("#responseBody").html("error:<pre>" + stripHTML(data.responseText)+"</pre>");
            }
        });
    });
});

function stripHTML(dirtyString) {
    let container = document.createElement('div');
    let text = document.createTextNode(dirtyString);
    container.appendChild(text);
    return container.innerHTML; // innerHTML will be a xss safe string
}

function randomString(len) {
    len = len || 32;
    var $chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz0123456789';    /****默认去掉了容易混淆的字符oOLl,9gq,Vv,Uu,I1****/
    var maxPos = $chars.length;
    var pwd = '';
    for (i = 0; i < len; i++) {
        pwd += $chars.charAt(Math.floor(Math.random() * maxPos));
    }
    return pwd;
}