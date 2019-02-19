//购物车控制层
app.controller('cartController', function ($scope, cartService) {
    // 获取购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                // 购物车商品列表
                $scope.cartList = response;
                // 商品总数和总金额
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        )
    };

    // 添加商品到购物车
    $scope.addGoodsToCartList = function (itemId, itemNum) {
        // 商品总数不能小于0
        // if ()
        cartService.addGoodsToCartList(itemId, itemNum).success(
            function (response) {
                if (response.success) {
                    // 刷新购物车列表
                    $scope.findCartList();
                } else {
                    alert(response.message);
                }
            }
        )
    };

    ///////////////////订单信息///////////////////////////////////

    // 选择付款方式
    $scope.order = {paymentType: "1"};
    $scope.selectPaymentType = function (type) {
        $scope.order.paymentType = type;
    };

    // 提交订单
    $scope.submitOrder = function () {
        // 补齐收件人信息
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;
        cartService.submitOrder($scope.order).success(
            function (response) {
                if (response.success) {
                    // 如果是微信支付，跳到支付页面
                    if ($scope.order.paymentType == "1") {
                        location.href = "pay.html";
                    } else {
                        // 如果是货到付款，直接结束
                        location.href = "paysuccess.html";
                    }
                } else {
                    alert(response.message);
                }
            }
        )
    };

    ///////////////////地址信息///////////////////////////////////

    // 获取收件地址
    $scope.findAddressListByUserId = function () {
        cartService.findAddressListByUserId().success(
            function (response) {
                $scope.addressList = response;
                // 判断是否为默认地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == "1") {
                        $scope.address = $scope.addressList[i];
                    }
                }
            })
    };

    // 记录用户选择的地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    };
    // 判断当前地址是否为用户选择的地址
    $scope.isSelected = function (address) {
        if ($scope.address == address) {
            return true;
        } else {
            return false;
        }
    };

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = cartService.update($scope.entity); //修改
        } else {
            serviceObject = cartService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.findAddressListByUserId();//重新加载
                } else {
                    alert(response.message);
                }
            }
        );
    };

    //删除
    $scope.dele = function (id) {
        cartService.dele(id).success(
            function (response) {
                if (response.success) {
                    $scope.findAddressListByUserId();//刷新列表
                }
            }
        );
    };

    //查询实体
    $scope.findOne = function (id) {
        cartService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }
});
