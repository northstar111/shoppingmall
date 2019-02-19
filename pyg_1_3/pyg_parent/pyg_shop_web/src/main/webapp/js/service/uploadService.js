app.service('uploadService',function($http){
    // 上传的方法
    this.uploadFile = function () {
        var formData = new FormData(); // 表单对象
        formData.append("file", file.files[0]); //将文件上传项追加到表单中
        return $http({
            url:"../uploadFile.do",
            method:"POST",
            data:formData,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity
        })
    }

});