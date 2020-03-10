$(document).ready(function () {
    let tag = document.title;
    let $tag = $(".nav-tag").children("li");
    $tag.removeAttr("class");
    if (tag === "全部漫画") {
        $tag.eq(0).attr("class", "active");
    }
    if (tag === "全部动画") {
        $tag.eq(1).attr("class", "active");
    }
    if (tag === "动漫资讯") {
        $tag.eq(2).attr("class", "active");
    }
    if (tag === "话题讨论") {
        $tag.eq(3).attr("class", "active");
    }
    $(".btn-search").on("click", function () {
        let test = $(".input-search").val();
        if (test === "") {
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
    return num === 0 ? number + '.0' : number;
}

//进度
function progress(num) {
    const str = ["暂无", "想看", "在看", "看过", "搁置", "已弃"];
    return str[num];
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
    return Y + "-" + M + "-" + D + " " + h + ":" + m + ":" + s;
}

//与当前时间比较后的时间格式化
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

//获取控件数组的值拼成字符串
function strArrE(arr) {
    let str = "";
    for (let i = 0; i < arr.length; i++) {
        str += arr[i].value;
        if (i !== arr.length - 1) {
            str += ",";
        }
    }
    return str;
}

//显示文件大小单位
function sizeStr(num) {
    if (num > (1024 * 1024)) {
        return Math.round(num / (1024 * 1024) * 10) / 10 + "M";
    }
    if (num > 1024) {
        return Math.round(num / 1024 * 10) / 10 + "K";
    }
    return Math.round(num * 10) / 10 + "B";
}
