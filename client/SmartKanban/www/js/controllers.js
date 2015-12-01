angular.module('starter.controllers', [])

.controller('LoginCtrl', function($scope, SmartKanbanService, $state, $ionicPopup, $ionicLoading, $ionicPlatform, $localstorage) {

    $scope.data = {};
    $scope.data.server = $localstorage.get('server');
    $scope.data.username = $localstorage.get('username');
    $scope.data.email = $localstorage.get('email');
    if($scope.data.server == undefined){
      $scope.data.server = "vw-pun-ngp-dv43:28080";
    }

    $scope.login = function() {
        if($scope.data.username == undefined || $scope.data.password == undefined ||  $scope.data.server == undefined || $scope.data.email == undefined){
            var alertPopup = $ionicPopup.alert({
                title: 'Login failed!',
                template: 'All fields are mandatory !'
            });
            return;
        }

        $localstorage.set('username', $scope.data.username);
        $localstorage.set('server', $scope.data.server);
        $localstorage.set('email', $scope.data.email);

        $ionicLoading.show({  template: '<div><ion-spinner icon="lines" class="assertive"></ion-spinner>Please wait...</div>' });
        SmartKanbanService.loginUser($scope.data.username, $scope.data.password, $scope.data.server, $scope.data.email)
        .then(function(data) {
            $ionicLoading.hide();
            $state.go('menu');
        },
        function(data) {
            $ionicLoading.hide();
            var alertPopup = $ionicPopup.alert({
                title: 'Login failed!',
                template: 'Please check your credentials!'
            });
        });
    };
})


.controller('MenuCtrl', function($scope,$state, $ionicHistory , $ionicLoading) {
    $ionicHistory.clearHistory();
})

.controller('PrintCtrl', function($scope, SmartKanbanService, $ionicLoading) {
    $scope.stickiesGenerated = false;
    $scope.data ={team: null, sprint: null};
    $scope.generateStickies = function(){
       $ionicLoading.show({  template: 'Please wait...' });
       SmartKanbanService.generateStickies($scope.data.team, $scope.data.sprint)
        .then(function(data) {
            //$state.go('tab.dash');
            //$cordovaPreferences.set('user_name', $scope.data.username);
            //$cordovaPreferences.set('kanbanserver_name', $scope.data.kanbanserver);
            $scope.stickiesGenerated = true;
            $ionicLoading.hide();
        },
        function(data) {
            $ionicLoading.hide();
            var alertPopup = $ionicPopup.alert({
                title: 'Stikcy generation failed',
                template: 'Please check your credentials!'
            });
        });      
    };
    
})


.controller('ProgressCtrl', function($scope, $cordovaCamera, SmartKanbanService, $ionicLoading) {

  var options = {
     // quality: 50,
      destinationType: Camera.DestinationType.DATA_URL,
      sourceType: Camera.PictureSourceType.CAMERA,
      //allowEdit: true,
      encodingType: Camera.EncodingType.JPEG,
      //popoverOptions: CameraPopoverOptions,
      saveToPhotoAlbum: false
    };



  $scope.takeAndUploadPicture = function(){
    options.sourceType = Camera.PictureSourceType.CAMERA;
    $scope.uploadPicture();
  };

  $scope.selectAndUploadPicture = function(){
    options.sourceType = Camera.PictureSourceType.PHOTOLIBRARY;
    $scope.uploadPicture();
  };  


  $scope.uploadPicture = function(){
    $cordovaCamera.getPicture(options).then(function(imageURI) {
      //var image = document.getElementById('myImage');
      //image.src = "data:image/jpeg;base64," + imageURI;
      $scope.image = imageURI;
      $ionicLoading.show({  template: 'Please wait...' });
      SmartKanbanService.uploadImage(imageURI).then(function(data){
          $ionicLoading.hide();
          alert(data);
      }, function(data){
         $ionicLoading.hide();
         alert(data); }
      );
    }, function(err) {
      // error
    });
  };

})

.controller("QuickViewCtrl", function($scope, $cordovaBarcodeScanner, SmartKanbanService, $ionicLoading) {
 
    //$scope.items = [{id: "SKP-123", status: "In Progress", type: "USER_STORY", assignee: "Smijith Pichappan", title : "This is test title to test the title", description: "This is for testing the tiltle and descriptin of task and for doing someting really interesting"}];
    $scope.items = [];
    $scope.scanBarcode = function() {
        $cordovaBarcodeScanner.scan().then(function(imageData) {
            //alert(imageData.text);
            console.log("Barcode Format -> " + imageData.format);
            console.log("Cancelled -> " + imageData.cancelled);
            $scope.getQuickView (imageData.text);

        }, function(error) {
            console.log("An error happened -> " + error);
        });

        //$scope.getQuickView ("TOM-12345");

    };

    $scope.getQuickView = function(itemId){
        //$scope.items.unshift({title: "fetching " + itemId});
        $ionicLoading.show({  template: 'Please wait...' });
        SmartKanbanService.getQuickView(itemId)
        .then(function(data) {
            $ionicLoading.hide();
            //alert(data.title);
            $scope.items.unshift(data);
        },
        function(data) {
            $ionicLoading.hide();
            var alertPopup = $ionicPopup.alert({
                title: 'Quick view failed',
                template: 'Please check your credentials!'
            });
        });   
    };
 
});
