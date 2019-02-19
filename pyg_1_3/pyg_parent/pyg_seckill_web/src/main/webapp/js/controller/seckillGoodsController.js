//控制层
app.controller('seckillGoodsController', function ($scope, $interval, $location, seckillGoodsService) {

    ////////////////////////////订单服务///////////////////////////
    // 提交秒杀订单
    $scope.submitOrder = function () {
        seckillGoodsService.submitOrder($scope.entity.id).success(
            function (response) {
                if (response.success) {
                    alert("提交成功，请在1分钟内完成支付");
                    location.href = "pay.html";
                } else {
                    alert(response.message);
                }
            }
        )
    };

    ////////////////////////////商品服务///////////////////////////

    // 获取秒杀商品列表
    $scope.findList = function () {
        seckillGoodsService.findList().success(
            function (response) {
                $scope.seckillGoodsList = response;
            }
        )
    };
    // 获取秒杀商品
    $scope.findOneFromRedis = function () {
        seckillGoodsService.findOneFromRedis($location.search()["id"]).success(
            function (response) {
                $scope.entity = response;
                // 倒计时处理
                // 剩余总秒数
                var allSeconds = Math.floor((new Date($scope.entity.endTime).getTime() - (new Date().getTime())) / 1000);
                // 定时任务
                var time = $interval(function () {
                    if (allSeconds > 0) {
                        allSeconds -= 1;
                        // 转为时间字符串
                        $scope.timeString = convertTimeString(allSeconds);
                    } else {
                        $interval.cancel(time);
                        alert("秒杀活动已结束！")
                    }
                }, 1000);
            }
        )
    };
    // 将秒数转为时间字符串，格式：X天 HH:mm:ss
    convertTimeString = function (allSeconds) {
        var day = Math.floor(allSeconds / (24 * 60 * 60));
        var hour = Math.floor((allSeconds - day * 24 * 60 * 60) / (60 * 60));
        var minute = Math.floor((allSeconds - day * 24 * 60 * 60 - hour * 60 * 60) / 60);
        var second = allSeconds - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60;
        var timeString = "";
        if (day>0) {
            timeString += day + "天 ";
        }
        return timeString + hour + ":" + minute + ":" + second;
    };
});
