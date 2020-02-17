$(document).ready(function () {
    let title = document.title;
    /*
    导航栏
     */
    $("#navbar-head").children("li").removeAttr("class");
    if (title == "动漫信息") {
        $("#nav-acInfo").attr("class", "active");
    }
    if (title == "动漫资讯") {
        $("#nav-acNews").attr("class", "active");
    }
    if (title == "用户管理") {
        $("#nav-user").attr("class", "active");
    }
    if (title == "留言管理") {
        $("#nav-comment").attr("class", "active");
    }
    /**跳页**/
    $("#pNum-btn").click(function () {
        let pNumStr = $("#pNum").text();
        let pNum = parseInt(pNumStr);
        let pTotalStr = $("#pTotal").text();
        let pTotal = parseInt(pTotalStr);
        let pSize = $("#pSize-text").attr("value");
        let toPage = $("#pNum-text").val();
        if (/^[0-9]*$/.test(toPage) && (toPage <= pTotal && toPage > 0)) {
            page(toPage, pSize);
        } else {
            $("#pNum-text").val(pNum);
            alert("请输入1-" + pTotal + "之间的整数！！");
        }
    });
    $("#pSize-btn").click(function () {
        let pNumStr = $("#pNum").text();
        let pNum = parseInt(pNumStr);
        let pSize = $("#pSize-text").attr("value");
        let pTotalStr = $("#pTotal").text();
        let pTotal = parseInt(pTotalStr);
        let pCount = parseInt($("#pCount").text());
        let input = $("#pSize-text").val();
        if (/^[0-9]*$/.test(input) && (input <= pCount && input > 0)) {
            page(1, input);
        } else {
            $("#pSize-text").val(pSize);
            alert("请输入1-" + pCount + "之间的整数！！");
        }
    });
    /**搜索**/
    $("#search-input").focus();
    $("#search-button").click(function () {
        var input = $("#search-input").val();
        if (input == "") {
            alert("不能为空! ");
            return false;
        }
        if (input == "%") {
            input = "%25";
        }
        window.location.href = pathName + "/search=" + input;
    });
    $("#search-input").keydown(function (e) {//当按下按键时
        if (e.which == 13) {//.which属性判断按下的是哪个键，回车键的键位序号为13
            $('#search-button').trigger("click");//触发搜索按钮的点击事件
        }
    });
    $("#pSize-text").keydown(function (e) {
        if (e.which == 13) {
            $('#pSize-btn').trigger("click");
        }
    });
});

//跳页
function clickPage(object) {
    let text = $(object).text();
    let pNumStr = $("#pNum").text();
    let pNum = parseInt(pNumStr);
    let pSizeStr = $("#pSize-text").attr("value");
    let pSize = parseInt(pSizeStr);
    let pTotalStr = $("#pTotal").text();
    let pTotal = parseInt(pTotalStr);
    if (text == "首页") {
        if (pNum <= 1) {
            alert("已经是第一页!");
            return false;
        }
        pNum = 1;
    } else if (text == "尾页") {
        if (pNum >= pTotal) {
            alert("已经是最后一页");
            return false;
        }
        pNum = pTotal;
    } else if (text == "上一页") {
        if (pNum <= 1) {
            alert("已经是第一页!");
            return false;
        }
        pNum--;
    } else if (text == "下一页") {
        if (pNum >= pTotal) {
            alert("已经是最后一页");
            return false;
        }
        pNum++;
    }
    page(pNum, pSize);
}

// 获取分页数据
function page(pNum, pSize) {
    window.location.href = pathName + "List" + pSize + "/p" + pNum;
}

//日期格式化
function date(timestamp) {
    let dates = new Date(timestamp);
    let Y = dates.getFullYear();
    let M = (dates.getMonth() + 1) < 10 ? '0' + (dates.getMonth() + 1) : dates.getMonth() + 1;
    let D = dates.getDate() < 10 ? '0' + dates.getDate() : dates.getDate();
    let date = Y + "/" + M + "/" + D;
    return date;
}


function status(num) {
    switch (num) {
        case 2:
            return "置顶";
        case 1:
            return "正常";
        case 0:
            return "隐藏";
        default:
            return "异常";
    }
}