$(document).ready(function () {
    let tag = document.title;
    $(".nav-tag").children("li").removeAttr("class");
    if (tag == "全部漫画") {
        $(".nav-tag").children("li").eq(0).attr("class", "active");
    }
    if (tag == "全部动画") {
        $(".nav-tag").children("li").eq(1).attr("class", "active");
    }
    if(tag=="动漫资讯"){
        $(".nav-tag").children("li").eq(2).attr("class","active");
    }
    $(".btn-search").click(function () {
        var test = $(".input-search").val();
        if (test == "") {
            alert("不能为空!");
        }
    });
});

//注销登录
function quit() {
    if (confirm("确认退出？")) {
        window.location.href = "/exit";
    }
}

//星星
function star(num) {
    if (num <= 0) {
        return "";
    }
    num = Math.round(num);
    let star = "";
    for (let i = 0; i < num; i++) {
        star += "★";
    }
    for (let i = num; i < 5; i++) {
        star += "☆";
    }
    return star;
}

function numFormat(number) {
    if (number <= 0) {
        return "未评分";
    }
    let num = (number * 10) % 10;
    return num == 0 ? number + '.0' : number;
}

//进度
function progress(num) {
    var strs = ["暂无", "想看", "在看", "看过", "搁置", "已弃"];
    return strs[num];
}

//时间格式化
function dateFormat(times) {
    let dates = new Date(times);
    let Y = dates.getFullYear();
    let M = dates.getMonth() + 1;
    let D = dates.getDate();
    let h = dates.getHours();
    let m = dates.getMinutes();
    let s = dates.getSeconds();
    let dateTime = Y + "-" + M + "-" + D + " " + h + ":" + m + ":" + s;
    return dateTime;
}

//与当前时间比较后
function fullTime(timestamp) {
    let now = new Date();
    let dates = new Date(timestamp);
    let Y = (dates.getFullYear() < now.getFullYear() ? dates.getFullYear() + '-' : '');
    let M = (dates.getMonth() + 1) < 10 ? '0' + (dates.getMonth() + 1) + '-' : (dates.getMonth() + 1) + '-';
    let D = dates.getDate() < 10 ? '0' + dates.getDate() : dates.getDate();
    let h = dates.getHours() + ':';
    let m = dates.getMinutes() + ':';
    let s = dates.getSeconds();
    return Y + M + D + " " + h + m + s;
}

