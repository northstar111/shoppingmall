//服务层
app.service('seckillGoodsService',function($http){
    // 提交秒杀订单
    this.submitOrder = function (seckillId) {
        return $http.get('../seckillOrder/submitOrder.do?seckillId=' + seckillId);
    };
    // 获取秒杀商品列表
    this.findList=function(){
        return $http.get('../seckillGoods/findList.do');
    };
    // 获取秒杀商品
    this.findOneFromRedis=function(id){
        return $http.get('../seckillGoods/findOneFromRedis.do?id='+id);
    };
});
