//购物车控制层
app.controller('payController', function ($scope, $location, payService) {
    // 生成微信二维码
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                // 返回结果
                $scope.total_fee = (response.total_fee / 100).toFixed(2);
                $scope.out_trade_no = response.out_trade_no;
                // 生成二维码
                var qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    value: response.code_url,
                    background:"#1A5798",
                    foreground:"#FFFFFF"
                });
                // 查询支付状态
                queryPayStatus($scope.out_trade_no);
            }
        )
    };
    // 检查支付状态
    queryPayStatus = function (out_trade_no) {
        payService.queryPayStatus(out_trade_no).success(
            function (response) {
                if (response.success) {
                    location.href = "paysuccess.html#?total_fee=" + $scope.total_fee;
                } else {
                    if (response.message == "支付超时") {
                        location.href = "payTimeOut.html";
                    } else {
                        location.href = "payfail.html";
                    }
                }
            }
        )
    };
    // 获取实付金额
    $scope.getPayment = function () {
        return $location.search()["total_fee"];
    }
});
