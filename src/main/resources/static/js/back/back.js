var pageName = "";

function page(pNum, pSize) {
    window.location.href = context + "/back/" + pageName + "/info/" + pNum + "?pageSize=" + pSize;
}

$(document).ready(function () {
    var title = document.title;
    /*
    导航栏
     */
    $("#navbar-head").children("li").removeAttr("class");
    if (title == "动漫信息") {
        $("#nav-acInfo").attr("class", "active");
        pageName = "ac";
    }
    if (title == "动漫资讯") {
        $("#nav-acNews").attr("class", "active");
        pageName = "acNews";
    }
    if (title == "用户管理") {
        $("#nav-user").attr("class", "active");
        pageName = "user";
    }
    if (title == "留言管理") {
        $("#nav-comment").attr("class", "active");
        pageName = "comment";
    }
    /*
    搜索
     */
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
        window.location.href = context + "/back/" + pageName + "/search=" + input;
    });
    $("#search-input").keydown(function (event) {
        if (event.keyCode == 13) {
            $("#search-button").click();
        }
    });
    /*
    设置页面显示条数
     */
    $("#changeSize-button").click(function () {
        var chSize = $("#changeSize-text").val();
        if (/^[0-9]*$/.test(chSize) && (chSize <= pCount && chSize > 0)) {
            page(1, chSize);
        } else {
            alert("请输入1-" + pCount + "之间的整数！！");
            $("#changeSize-text").val(pSize);
        }
    });
    /*
    跳页
     */
    $("#turnPage-button").click(function () {
        var tPage = $("#turnPage-text").val();
        if (/^[0-9]*$/.test(tPage) && (tPage <= pTotal && tPage > 0)) {
            page(tPage, pSize);
        } else {
            alert("请输入1-" + pTotal + "之间的整数！！");
            $("#turnPage-text").val(pNum);
        }
    });
    if (pNum == 1) {
        $(".ul-page li:first-child").attr("class", "disabled");
        $("#prePage-li").attr("class", "disabled");
    } else {
        $(".ul-page li:first-child").removeAttr("class");
        $("#prePage-li").removeAttr("class");
    }
    if (pNum == pTotal) {
        $(".ul-page li:last-child").attr("class", "disabled");
        $("#nextPage-li").attr("class", "disabled");
    } else {
        $(".ul-page li:last-child").removeAttr("class");
        $("#nextPage-li").removeAttr("class");
    }
});
$("#search-button").click(function () {
    var input = $("#search-input").val();
    if (input == "") {
        alert("不能为空! ")
    } else {
        window.location.href = "/back/find?s=" + input;
    }
});

function clickPage(obj) {
    var text = $(obj).text();
    if (text == "首页") {
        if (pNum > 1) {
            page(1, pSize);
        } else {
            return false;
        }
    } else if (text == "尾页") {
        if (pNum < pTotal) {
            page(pTotal, pSize);
        } else {
            return false;
        }
    } else if (text == "上一页") {
        if (pNum > 1) {
            page(pNum - 1, pSize);
        } else {
            return false;
        }
    } else if (text == "下一页") {
        if (pNum < pTotal) {
            page(pNum + 1, pSize);
        } else {
            return false;
        }
    } else if (/^[0-9]*$/.test(text)) {
        if (text != pNum) {
            page(text, pSize);
        } else {
            return false;
        }
    }
}
