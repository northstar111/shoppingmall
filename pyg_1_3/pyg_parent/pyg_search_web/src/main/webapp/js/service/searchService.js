// 前台搜索服务
app.service("searchService",function($http){
    // 搜索
    this.search = function (searchMap) {
        return $http.post("itemSearch/search.do",searchMap);
    }
});