// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
angular.module('starter', ['ionic', 'starter.controllers', 'starter.services', 'ngCordova'])

.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if (window.cordova && window.cordova.plugins && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      cordova.plugins.Keyboard.disableScroll(true);

    }
    if (window.StatusBar) {
      // org.apache.cordova.statusbar required
      StatusBar.styleLightContent();
    }
  });
})

.config(function($stateProvider, $urlRouterProvider, $ionicConfigProvider, $httpProvider) {

  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js

  $ionicConfigProvider.tabs.position('bottom');
  $httpProvider.defaults.headers.common = { 'Content-Type' : 'application/json', "crossDomain" : true, 'Access-Control-Allow-Origin' : "*"};

  $stateProvider

  //Login 
  .state('login', {
      url: '/login',
      templateUrl: 'templates/login.html',
      controller: 'LoginCtrl'
  })

  //Main menu 
  .state('menu', {
      url: '/menu',
      templateUrl: 'templates/menucards.html',
      controller: 'MenuCtrl'
  })

  .state('quickview', {
      url: '/quickview',
      templateUrl: 'templates/tab-quickview.html',
      controller: 'QuickViewCtrl'
  })

  .state('print', {
      url: '/print',
      templateUrl: 'templates/tab-print.html',
      controller: 'PrintCtrl'
    })

  .state('progress', {
      url: '/progress',
      templateUrl: 'templates/tab-progress.html',
      controller: 'ProgressCtrl'
    })

  .state('settings', {
    url: '/settings',
    templateUrl: 'templates/tab-account.html',
    controller: 'AccountCtrl'
  })

  // setup an abstract state for the tabs directive
    .state('tab', {
    url: '/tab',
    abstract: true,
    templateUrl: 'templates/tabs.html'
  })

    .state('tab.quickview', {
      url: '/quickview',
      views: {
        'tab-quickview': {
          templateUrl: 'templates/tab-quickview.html',
          controller: 'QuickViewCtrl'
        }
      }
    })

    .state('tab.print', {
      url: '/print',
      views: {
        'tab-print': {
          templateUrl: 'templates/tab-print.html',
          controller: 'QuickViewCtrl'
        }
      }
    })

    .state('tab.progress', {
      url: '/progress',
      views: {
        'tab-progress': {
          templateUrl: 'templates/tab-progress.html',
          controller: 'ProgressCtrl'
        }
      }
    })

  .state('tab.account', {
    url: '/account',
    views: {
      'tab-account': {
        templateUrl: 'templates/tab-account.html',
        controller: 'AccountCtrl'
      }
    }
  });



  // if none of the above states are matched, use this as the fallback
  //$urlRouterProvider.otherwise('/tab/dash');
  $urlRouterProvider.otherwise('/login');

});
