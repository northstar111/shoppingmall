//支付服务层
app.service('payService', function ($http) {
    // 生成微信二维码
    this.createNative = function () {
        return $http.get('../pay/createNative.do');
    };
    // 检查支付状态
    this.queryPayStatus = function (out_trade_no) {
        return $http.get('../pay/queryPayStatus.do?out_trade_no='+out_trade_no);
    };
});
