// 自定义服务
// 参数1：服务名称
// 参数2：function
app.service("brandService", function ($http) {
    // 查询品牌列表
    this.findAll = function () {
        return $http.get('../brand/findAll.do');
    };
    // 分页
    this.findPage = function (page, rows) {
        return $http.get('../brand/findPage.do?page=' + page + '&rows=' + rows);
    };
    // 回显
    this.findBrand = function (id) {
        return $http.get("../brand/findBrand.do?id="+id);
    };
    // 批量删除
    this.dele = function (ids) {
        return $http.get("../brand/delete.do?ids="+ids);
    };
    // 分页条件查询
    this.search = function (page, rows, entity) {
        return $http.post("../brand/search.do?page="+page+"&rows="+rows, entity);
    };
    // 添加品牌
    this.add = function (entity) {
        return $http.post("../brand/add.do", entity);
    };
    // 修改品牌
    this.update = function (entity) {
        return $http.post("../brand/update.do", entity);
    };
    // 选择选项列表
    this.selectOptionList = function () {
        return $http.get('../brand/selectOptionList.do');
    }
});