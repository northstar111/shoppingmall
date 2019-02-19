// 定义控制器
app.controller('brandController', function ($scope, $controller, brandService) {

    // 伪继承
    // 参数1：被继承的控制器名称
    // 参数2：对象
    $controller("baseController", {$scope:$scope});

    // // 品牌名称列表
    // $scope.brandList = {
    //     data: [{id:1,text:'华为'},{id:2,text:'三星'},{id:3,text:'苹果'},{id:4,text:'魅族'}]
    //     // 其他配置略，可以去看看内置配置中的ajax配置
    // };

    // 获取品牌列表，后续使用分页查询
    $scope.findAll = function () {
        // ../获取根目录
        brandService.findAll().success(
            function (response) {
                // 获取品牌数据
                $scope.list = response;
            }
        );
    };

    // 分页参数
    // $scope.paginationConf = {
    //     // 添加笔记内容
    //     // onChange，只要任一属性有变化时执行
    //     // 注意添加findPage参数
    //     // 页面一加载就执行，总条数参数变化后又执行一遍
    //     // totalItems=0时只查一次
    //     currentPage: 1,
    //     totalItems: 0,
    //     itemsPerPage: 10,
    //     perPageOptions: [10, 20, 30, 40, 50],
    //     onChange:function () {
    //         $scope.reloadList();
    //     }
    // };
    //
    // // 抽取刷新页面方法
    // $scope.reloadList = function () {
    //     $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    // };

    // 获取分页
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(
            function (response) {
                // 分页数据
                $scope.list = response.rows;
                // 总记录数给分页控件
                $scope.paginationConf.totalItems = response.total;
            }
        )
    };

    // 按条件查询分页
    // 搜索的商品对象
    $scope.brandEntity = {};
    $scope.search = function (page, rows) {
        brandService.search(page, rows, $scope.brandEntity).success(
            function (response) {
                // 分页数据
                $scope.list = response.rows;
                // 总记录数给分页控件
                $scope.paginationConf.totalItems = response.total;
            }
        );
    };


    // 保存，包括添加和修改
    // $scope.entity = {};
    $scope.save = function () {
        var methodObject = null;
        if ($scope.entity.id != null){
            methodObject = brandService.update($scope.entity);
        } else {
            methodObject = brandService.add($scope.entity);
        }
        methodObject.success(
            function (response) {
                if (response.success) {
                    // 刷新页面
                    $scope.reloadList();
                } else {
                    // 提示失败
                    alert(response.message);
                }
            }
        )
    };

    // 查询要修改的商品
    $scope.findBrand = function (id) {
        brandService.findBrand(id).success(
            function (response) {
                $scope.entity = response;
            }
        )
    };

    // // 选中的id数组
    // $scope.selectIds = [];
    // // 更新选中商品id
    // $scope.updateSelection = function ($event, id) {
    //     if ($event.target.checked){
    //         // 如果被选中，就添加进数组
    //         $scope.selectIds.push(id);
    //     } else {
    //         var index = $scope.selectIds.indexOf(id);
    //         $scope.selectIds.splice(index,1);
    //     }
    // };
    // 删除商品
    $scope.deleteBrand = function () {
        brandService.dele($scope.selectIds).success(
            function (response) {
                if (response.success){
                    // 清空商品id数组
                    $scope.selectIds = [];
                    // 刷新页面
                    $scope.reloadList();
                } else {
                    alert(response.method);
                }
            }
        )
    }

});