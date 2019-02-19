//控制层
app.controller('goodsController', function ($scope, $controller, $location, typeTemplateService, itemCatService, uploadService, goodsService) {

    $controller('baseController', {$scope: $scope});//继承

    // 商品审核状态
    $scope.status = ["未审核", "审核通过", "审核未通过", "已关闭"];

    // 商品分类数组
    // 1000条数据可接受，太多不行
    $scope.itemCatList = [];

    $scope.findItemCatList = function () {
        itemCatService.findAll().success(
            function (response) {
                for (var i = 0; i < response.length; i++) {
                    $scope.itemCatList[response[i].id] = response[i].name;
                }
            })
    };

    // 定义组合实体类
    $scope.entity = {goods: {isEnableSpec: "1"}, goodsDesc: {itemImages: [], specificationItems: []}, itemList: []};

    // 重在理解

    // 判断当前规格选项在勾选结果中是否存在
    // 存在：返回true
    // 不存在：返回false
    $scope.isChecked  = function (name, value) {
        var specitems = $scope.entity.goodsDesc.specificationItems; // 勾选结果
        var obj = selectObject(specitems, "attributeName", name);
        // 该规格已被勾选
        if (obj != null) {
            if (obj.attributeValue.indexOf(value) >= 0) {    // 该规格选项被勾选
                return true;
            }
        }
        return false;

    };

    /////////////////////////////// 生成规格行 ////////////////////////////////
    // 已知勾选结果
    // [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G","联通3G"]},{"attributeName":"屏幕尺寸","attributeValue":["5.5寸","6寸"]}]
    // 每次勾选，重新生成SKU列表
    $scope.createItemList = function () {
        var specitems = $scope.entity.goodsDesc.specificationItems;
        $scope.entity.itemList = [{spec: {}, price: 0, num: 99999, status: "1", isDefault: "0"}];
        // 遍历勾选结果
        for (var i = 0; i < specitems.length; i++) {
            // 上一次的list和本次循环的规格生成新的list
            $scope.entity.itemList = addColumn($scope.entity.itemList, specitems[i].attributeName, specitems[i].attributeValue);
        }

    };

    // list:[{spec:{}, price:0, num:99999, status:"1", isDefault:0}]
    // value:attributeValue
    addColumn = function (list, name, values) {
        var newList = [];
        for (var i = 0; i < list.length; i++) {
            var oldRow = list[i];
            for (var j = 0; j < values.length; j++) {
                var newRow = JSON.parse(JSON.stringify(oldRow));	//json深克隆
                newRow.spec[name] = values[j];
                newList.push(newRow);
            }
        }
        return newList;
    };

    /////////////////////////////// 规格面板勾选 ////////////////////////////////
    // 记录勾选结果
    // name：规格名称
    // value：选项名称
    $scope.updateSpecAttribute = function ($event, name, value) {
        var specItems = $scope.entity.goodsDesc.specificationItems;	//勾选结果
        var obj = selectObject(specItems, "attributeName", name);
        // 该规格已被勾选，向属性数组中添加元素
        // 被勾选的元素才会移除
        if (obj != null) {
            if ($event.target.checked) {
                obj.attributeValue.push(value);
            } else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(value), 1);
                // 判断选项对象是否为空，空的话移除对象
                if (obj.attributeValue.length == 0) {
                    specItems.splice(specItems.indexOf(obj), 1);
                }
            }
        } else {	// 向勾选结果数组中添加对象
            specItems.push({"attributeName": name, "attributeValue": [value]});
        }
    };

    // 判断要勾选的规格名称在勾选结果中是否存在
    // 存在：添加元素
    // 不存在：添加对象
    // list:$scope.entity.goodsDesc.specificationItems
    // name:规格名称，就是网络制式
    selectObject = function (list, key, name) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key] == name) {	// 说明要勾选的规格名称已经在勾选结果中存在
                return list[i];
            }
        }
        return null;	// 该规格没有被勾选返回null
    };

    // 图片上传
    $scope.uploadFile = function () {
        uploadService.uploadFile().success(
            function (response) {
                if (response.success) {
                    $scope.image_entity.url = response.message;
                } else {
                    alert(response.message);
                }
            }
        )
    };

    // 图片对象
    $scope.image_entity = {};

    // 向图片数组添加对象
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };

    // 从图片数组删除对象
    $scope.remove_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    //读取列表数据绑定到表单中
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    //查询实体
    $scope.findOne = function () {
        // 从url解析id
        // url格式：路由写法“#”
        var id = $location.search()["id"];
        if(id!=null){   // 修改商品
            goodsService.findOne(id).success(
                function (response) {
                    $scope.entity = response;
                    // 显示介绍
                    editor.html($scope.entity.goodsDesc.introduction);
                    // 显示图片
                    $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                    // 显示规格选项
                    $scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);
                    // 显示规格选项列表
                    for (var i=0; $scope.entity.itemList.length;i++){
                        $scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec);
                    }
                }
            );
        }
    };


    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        $scope.entity.goodsDesc.introduction = editor.html(); // 富文本内容赋值给商品介绍字段
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    // 方式一：跳转到商品列表页	--推荐
                    location.href = "/admin/goods.html";
                    // 方式二：清空当前页，继续添加

                } else {
                    alert(response.message);
                }
            }
        );
    }


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                }
            }
        );
    }

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    // 查询第一级分类列表
    $scope.findCategory1List = function () {
        itemCatService.findByParentId(0).success(
            function (response) {	// 第一级分类
                $scope.category1List = response;
            }
        )
    }

    // 查询第二级分类列表
    // $watch 监控变量方法
    // 参数1：被监控变量
    // 参数2：function，变量发生变化时执行
    $scope.$watch("entity.goods.category1Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {	// 第一级分类
                $scope.category2List = response;
                // $scope.category3List = null;	// 影响后续操作，先不做
            }
        )
    });

    // 查询第三级分类列表
    // $watch 监控变量方法
    // 参数1：被监控变量
    // 参数2：function，变量发生变化时执行
    $scope.$watch("entity.goods.category2Id", function (newValue, oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {	// 第一级分类
                $scope.category3List = response;
            }
        )
    });

    // 选择第三级分类时，查询模板id
    // 根据主键查询某个第三级分类对象，里边含有分类id
    $scope.$watch("entity.goods.category3Id", function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(
            function (response) { // 第三级分类对象
                $scope.entity.goods.typeTemplateId = response.typeId;
            }
        );
    });

    // 监控模板id发生变化时，查询模板对象，获取品牌和规格数据
    $scope.$watch("entity.goods.typeTemplateId", function (newValue, oldValue) {
        // 可通过findOne获取品牌数据
        typeTemplateService.findOne(newValue).success(
            function (response) {	// 模板对象
                // 转换json字符串
                $scope.brandList = JSON.parse(response.brandIds);
            }
        );
        // 定义新方法，根据模板id
        typeTemplateService.findSpecList(newValue).success(
            function (response) { // 模板数据
                $scope.specList = response;
            }
        )
    });


});
