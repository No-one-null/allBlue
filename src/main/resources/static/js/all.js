const ratingArr=["请评价!","很差","较差","还行","推荐","力荐"];
const maxSize = 1024 * 1024;

//不同浏览器获取上传图片路径
function getObjectURL(file) {
    let url = null;
    if (window.createObjectURL !== undefined) { // basic
        url = window.createObjectURL(file);
    } else if (window.URL !== undefined) { // mozilla(firefox)
        url = window.URL.createObjectURL(file);
    } else if (window.webkitURL !== undefined) { // webkit or chrome
        url = window.webkitURL.createObjectURL(file);
    }
    return url;
}

//搜索
function search(word, conditions) {
    if (word === '') {
        alert("不能为空!");
        return;
    }
    location.href = searchUrl + "/" + word + "?conditions=" + conditions;
}