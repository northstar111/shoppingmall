// 新建login服务，获取用户登陆信息
app.service("loginService", function ($http) {
    this.loginName = function () {
        return $http.get("../loginName.do");
    }
});