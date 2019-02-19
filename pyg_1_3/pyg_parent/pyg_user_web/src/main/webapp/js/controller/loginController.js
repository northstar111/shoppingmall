 //控制层 
app.controller('loginController' ,function($scope,loginService){
    // 获取登录用户信息
	$scope.findLoginUser = function () {
        loginService.findLoginUser().success(
			function (response) {
			$scope.loginName = response.loginName;
        })
    };

});
