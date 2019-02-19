// 定义前台广告控制器
app.controller('contentController', function ($scope, contentService) {

    // 广告列表，按广告类别存储
    $scope.contentList = [];

    // 获取分类广告
    $scope.findListByCategoryId = function (categoryId) {
        contentService.findListByCategoryId(categoryId).success(
            function (response) {
                $scope.contentList[categoryId] = response;
            }
        )
    };

    // 获取用户输入的关键词，跳转到搜索页面
    $scope.search = function () {
        location.href = "http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});