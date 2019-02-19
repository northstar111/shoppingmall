 //控制层 
app.controller('userController' ,function($scope,userService){
	// 初始化entity对象
    $scope.entity = {};
	// 注册
	$scope.register = function () {
        // 用户名不能为空
        if ($scope.entity.username==null || $scope.entity.username==""){
            alert("请输入用户名");
            return;
        }
		// 密码不能为空
        if ($scope.entity.password==null || $scope.entity.password==""){
            alert("请输入密码");
            return;
        }
		// 校验密码是否一致
		if ($scope.entity.password!=$scope.password2){
            alert("密码不一致");
            return;
		}
        // 手机号不能为空
        if ($scope.entity.phone==null || $scope.entity.phone==""){
            alert("请输入手机号");
            return;
        }
		// 验证码不能为空
		if ($scope.checkCode==null || $scope.checkCode==""){
            alert("请输入验证码");
            return;
		}

		userService.add($scope.entity,$scope.checkCode).success(
			function (response) {
				if (response.success){
                    alert(response.message);
				} else {
                    alert(response.message);
				}
            }
		)
    };

    // 获取短信验证码
	$scope.sendCode = function () {
		// 检查手机号是否合法
		if ($scope.entity.phone==null || $scope.entity.phone==""){
            alert("请输入您的手机号");
            return;
		}
		userService.sendCode($scope.entity.phone).success(
			function (response) {
                if (response.success){
                    alert(response.message);
                } else {
                    alert(response.message);
                }
            }
		)
    };
});
