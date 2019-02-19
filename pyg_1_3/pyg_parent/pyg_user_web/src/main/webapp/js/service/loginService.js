//服务层
app.service('loginService',function($http){
	// 获取登录用户信息
	this.findLoginUser = function () {
        return $http.get('../login/findLoginUser.do');
    }
});
