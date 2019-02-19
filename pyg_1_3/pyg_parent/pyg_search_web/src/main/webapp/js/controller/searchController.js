// 前台搜索控制器
app.controller("searchController", function ($scope, $location, searchService) {
    // 搜索条件
    $scope.searchMap = {
        "keywords": "", "category": "", "brand": "", "spec": {}, "price": "",
        "pageNo": 1, "pageSize": 20, "sortField": "", "sortMethod": ""
    };

    //判断关键字里面是否包含品牌
    $scope.keywordsIsBrand = function () {
        var brandList = $scope.resultMap.brandList;
        for (var i = 0; i < brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf(brandList[i].text) >= 0) {
                //包含品牌
                return true;
            }
        }
        return false;
    };

    //接收首页传递的参数,进行搜索查询
    $scope.loadKeywords = function () {

        //获取传递的参数
        $scope.searchMap.keywords = $location.search()['keywords'];
        if ($scope.searchMap.keywords == '' || $scope.searchMap.keywords == null) {
            return;
        }
        //进行搜索
        $scope.search();

    };

    // 排序
    $scope.sortByField = function (sortField, sortMethod) {
        $scope.searchMap.sortField = sortField;
        $scope.searchMap.sortMethod = sortMethod;
        $scope.search();
    };

    //判断关键字里面是否包含品牌
    $scope.keywordsIsBrand = function () {
        var brandList = $scope.resultMap.brandList;
        for (var i = 0; i < brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf(brandList[i].text) >= 0) {
                //包含品牌
                return true;
            }
        }
        return false;
    };

    // 添加搜索条件
    $scope.addSearchItem = function (key, value) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        // 搜索条件变动后，需要重新搜索
        $scope.search();
    };

    // 移除搜索条件
    $scope.removeSearchItem = function (key) {
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        // 搜索条件变动后，需要重新搜索
        $scope.search();
    };

    // 搜索
    $scope.search = function () {

        // 页码类型转换
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);

        searchService.search($scope.searchMap).success(
            function (response) {
                // 返回商品列表
                $scope.resultMap = response;
                // 分页显示
                buildPageLabel();
            }
        )
    };

    // 构建分页
    buildPageLabel = function () {
        // 起始页码
        var startPage = 1;
        var endPage = $scope.resultMap.totalPages;
        // 显示前后点
        $scope.showFrontDot = false;
        $scope.showBehindDot = false;
        // 根据不同情况显示页码，最多显示5个
        if (endPage > 5) {
            if ($scope.searchMap.pageNo <= 3) {
                endPage = 5;
                $scope.showBehindDot = true;
            } else if ($scope.searchMap.pageNo >= endPage - 2) {
                startPage = endPage - 4;
                $scope.showFrontDot = true;
            } else {
                startPage = $scope.searchMap.pageNo - 2;
                endPage = $scope.searchMap.pageNo + 2;
                $scope.showFrontDot = true;
                $scope.showBehindDot = true;
            }
        }

        // 填充页码数组
        $scope.pageLabel = [];
        for (var i = startPage; i <= endPage; i++) {
            $scope.pageLabel.push(i)
        }
    };

    // 分页查询
    $scope.queryByPage = function (page) {
        $scope.searchMap.pageNo = page;
        $scope.search();
    };

    // 跳转到搜索页
    $scope.pageTo = 1;
    $scope.searchToPage = function () {
        $scope.searchMap.pageNo = $scope.pageTo;
        $scope.search();
    };

    // 判断是否到达边界页
    $scope.isEdgePage = function () {
        if ($scope.searchMap.pageNo == 1) {
            return "start";
        }
        if ($scope.searchMap.pageNo == $scope.resultMap.totalPages) {
            return "end";
        }
    };

});