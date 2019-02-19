//服务层
app.service('userService',function($http){
	//增加
	this.add=function(entity,checkCode){
		return  $http.post('../user/add.do?checkCode='+checkCode,entity );
	};

	// 发送短信验证码
	this.sendCode = function (phone) {
        return $http.get('../user/sendCode.do?phone=' + phone);
    };
});
