angular.module('starter.services', [])

.service('SmartKanbanService', function($q, $http) {
    var authAttrs = {};
    var server;
    var email;

    var dataURItoBlob = function (dataURI) {
     // convert base64/URLEncoded data component to raw binary data held in a string
     var byteString = atob(dataURI.split(',')[1]);
     var mimeString = dataURI.split(',')[0].split(':')[1].split(';')[0]

     var ab = new ArrayBuffer(byteString.length);
     var ia = new Uint8Array(ab);
     for (var i = 0; i < byteString.length; i++)
     {
        ia[i] = byteString.charCodeAt(i);
     }

     var bb = new Blob([ab], { "type": mimeString });
     return bb;
    };

    return {
        loginUser: function(name, pw, serverName, emailId) {
            var deferred = $q.defer();
            server = serverName;
            email = emailId;
            $http.post('http://' + server + '/smartkanban/rest/login', {loginId : name, password : pw})
            .success(function(resp) {
                console.log('Success', resp);
                authAttrs = resp.attributes;
                deferred.resolve('Welcome ' + name + '!');
            }).error(function(err) {
                console.error('ERR', err);
                deferred.reject('Wrong credentials.' + err);
            });

            return deferred.promise;
        },

        generateStickies : function(team, sprint, project){
            var deferred = $q.defer();
            var postData = {"team" : team,  "sprint" : sprint,  "project" : "TOM", "authAttrs" : authAttrs, emailId : email};
            $http.post('http://' + server + '/smartkanban/rest/kanban/generate', postData)
            .success(function(resp) {
                console.log('Success', resp);
                deferred.resolve(resp);
            }).error(function(err) {
                console.error('ERR', err);
                deferred.reject('Error .' + err);
            });
         
            return deferred.promise;
        },
        

        getQuickView : function(itemId){
            var deferred = $q.defer();
            $http.post('http://' + server + '/smartkanban/rest/quickview/' + itemId , authAttrs)
            .success(function(resp) {
                console.log('Success', resp);
                deferred.resolve(resp);
            }).error(function(err, code) {
                console.error('ERR', err);
                deferred.reject('Error .' + err);
            });
           
            return deferred.promise;
        },

        uploadImage: function(imageURI){
            var deferred = $q.defer();
            var fd = new FormData();
            fd.append('uploadFile', dataURItoBlob("data:image/jpeg;base64,"+imageURI));
            $http.post('http://' + server + '/smartkanban/rest/kanban/imageupload', fd, {

                transformRequest:angular.identity,
                  headers:{'Content-Type':undefined, 'Accept-Encoding' : 'multipart/form-data'
                }
            })
            .success(function(data, status, headers){
                postData = {authAttrs : authAttrs, fileName : data.fileName, requestId: data.requestId, async : true};
                $http.post('http://' + server + '/smartkanban/rest/kanban/decode', postData);  
                deferred.resolve("Image uploaded successfully.");
            })
            .error(function(data, status, headers){
                deferred.resolve("Image upload failed, please retry !");
            })

            return deferred.promise;
        }
    };

    
})

.factory('$localstorage', ['$window', function($window) {
  return {
    set: function(key, value) {
      $window.localStorage[key] = value;
    },
    get: function(key, defaultValue) {
      return $window.localStorage[key] || defaultValue;
    },
    setObject: function(key, value) {
      $window.localStorage[key] = JSON.stringify(value);
    },
    getObject: function(key) {
      return JSON.parse($window.localStorage[key] || '{}');
    }
  }
}]);


