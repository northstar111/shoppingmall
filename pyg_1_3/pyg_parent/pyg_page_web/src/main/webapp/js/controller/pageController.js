// 页面控制器
app.controller("pageController", function ($scope, $http) {
    // 加载SKU，同时深度克隆，防止修改规格时影响sku列表
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specification = JSON.parse(JSON.stringify($scope.sku.spec));

    };

    ///////////////////////////////动态效果////////////////////////////////////////
    // 修改加入购物车的商品数量
    $scope.goodsNum = 1;
    $scope.changeNum = function (num) {
        $scope.goodsNum += num;
        if ($scope.goodsNum < 1) {
            $scope.goodsNum = 1;
        }
    };

    // 记录选择的规格选项
    $scope.specification = {};
    // 选择规格，显示对应信息
    $scope.selectSpecification = function (key, value) {
        // 记录选择的规格
        $scope.specification[key] = value;
        // 查找对应的信息
        for (var i=0; i<skuList.length; i++){
            if (matchObject(skuList[i].spec,$scope.specification)){
                $scope.sku = skuList[i];
                return ;
            } else {
                // 查不到时的默认显示值
                $scope.sku = {id:0, title:">>>>>>>该商品不存在<<<<<<<<", price:0};
            }
        }
    };

    // 判断当前选项是否为用户选择的
    $scope.isSelected = function (key, value) {
        if ($scope.specification[key] == value){
            return true;
        }
        return false;
    };

    // 比较两个对象内容是否一致
    matchObject = function (map1, map2) {
        // 正向遍历
        for (var key1 in map1){
            if (map1[key1]!=map2[key1]){
                return false;
            }
        }
        // 反向遍历
        for (var key2 in map2){
            if (map1[key2]!=map2[key2]){
                return false;
            }
        }
        return true;
    };

    // 将SKU商品id和选择的商品数量加入购物车
    $scope.addToCart = function () {
        // alert("商品id："+$scope.sku.id+"，商品数量："+$scope.goodsNum);
        // 注意：跨域访问需要全路径
        // withCredentials属性：携带cookie访问
        $http.get("http://localhost:9107/cart/addGoodsToCartList.do?itemId=" + $scope.sku.id + "&itemNum=" + $scope.goodsNum,
            {"withCredentials":true}).success(
            function (response) {
                if (response.success){
                    // 成功后，跳转到购物车页面
                    location.href = "http://localhost:9107/cart.html";
                } else {
                    alert(response.message);
                }
            }
        )
    };
});