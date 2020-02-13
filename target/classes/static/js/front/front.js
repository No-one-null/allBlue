/*
注销登录
 */
function quit() {
    if (confirm("确认退出？")) {
        window.location.href = "/exit";
    }
}

$(document).ready(function () {
    var tag = document.title;
    $(".nav-tag").children("li").removeAttr("class");
    if (tag == "全部漫画") {
        $(".nav-tag").children("li").eq(0).attr("class", "active");
    }
    if (tag == "全部动画") {
        $(".nav-tag").children("li").eq(1).attr("class", "active");
    }
    $(".btn-search").click(function () {
        var test = $(".input-search").val();
        if (test == "") {
            alert("不能为空!");
        }
    });
});
