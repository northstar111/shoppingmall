// 定义index控制器
app.controller('indexController', function ($scope, loginService) {
    // 获取用户登陆信息
    $scope.loginName = function () {
        loginService.loginName().success(
            function (response) {
                $scope.loginName = response.loginName;
        })
    }
});