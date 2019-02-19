// 定义公共控制器
app.controller("baseController", function ($scope) {
    // 分页参数
    $scope.paginationConf = {
        // 添加笔记内容
        // onChange，只要任一属性有变化时执行
        // 注意添加findPage参数
        // 页面一加载就执行，总条数参数变化后又执行一遍
        // totalItems=0时只查一次
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange:function () {
            $scope.reloadList();
        }
    };

    // 抽取刷新页面方法
    $scope.reloadList = function () {
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    };

    // 选中的id数组
    $scope.selectIds = [];
    // 更新选中商品id
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked){
            // 如果被选中，就添加进数组
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    };

    // Json转换成字符串
    $scope.jsonToString = function (jsonStr, key) {
        var value = "";
        var json = JSON.parse(jsonStr);
        for (var i=0; i<json.length; i++){
            if (i>0){
                value += "，";
            }
            value += json[i][key];
        }
        return value;
    }

});