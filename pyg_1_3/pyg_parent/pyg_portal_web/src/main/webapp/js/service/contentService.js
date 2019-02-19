// 定义前台广告服务
app.service('contentService',function($http){
    // 获取分类广告
    this.findListByCategoryId = function (categoryId) {
        return $http.get('/content/findListByCategoryId.do?categoryId='+categoryId);
    }
})