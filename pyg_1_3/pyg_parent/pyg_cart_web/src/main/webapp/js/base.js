// 品优购模块
var app=angular.module('pyg',[]);

// 公共过滤器
app.filter("trustHtml", ["$sce", function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);