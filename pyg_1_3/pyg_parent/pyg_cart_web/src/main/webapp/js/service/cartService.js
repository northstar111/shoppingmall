//购物车服务层
app.service('cartService', function ($http) {
    // 获取购物车列表
    this.findCartList = function () {
        return $http.get('../cart/findCartList.do');
    };
    // 添加商品到购物车列表
    this.addGoodsToCartList = function (itemId, itemNum) {
        return $http.get('../cart/addGoodsToCartList.do?itemId=' + itemId + '&itemNum=' + itemNum);
    };
    // 计算商品总数和订单总金额
    this.sum = function (cartList) {
        // 购物车总计
        // totalItem：商品种类之和
        // totalNum：商品数量之和
        // totalMoney：商品总金额
        var totalValue = {totalItem:0, totalNum: 0, totalMoney: 0.00};
        if (cartList != null) {
            for (var i = 0; i < cartList.length; i++) {
                for (var j = 0; j < cartList[i].orderItemList.length; j++) {
                    totalValue.totalItem += 1;
                    totalValue.totalNum += cartList[i].orderItemList[j].num;
                    totalValue.totalMoney += cartList[i].orderItemList[j].totalFee;
                }
            }
        }
        return totalValue;
    };

    /////////////////////订单信息/////////////////////////////////////////
    // 提交订单
    this.submitOrder = function (order) {
        return $http.post("../order/add.do",order);
    };

    /////////////////////地址信息/////////////////////////////////////////
    // 获取收件地址
    this.findAddressListByUserId = function () {
        return $http.get('../address/findAddressListByUserId.do');
    };
    //增加
    this.add = function (entity) {
        return $http.post('../address/add.do', entity);
    };
    //修改
    this.update = function (entity) {
        return $http.post('../address/update.do', entity);
    };
    //删除
    this.dele=function(id){
        return $http.get('../address/delete.do?id='+id);
    };
    //查询实体
    this.findOne=function(id){
        return $http.get('../address/findOne.do?id='+id);
    }
});
