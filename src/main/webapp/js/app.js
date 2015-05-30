$(document).ready(function(){
  $(".button-collapse").sideNav();
});
(function() {
  'use strict';

  var app= angular.module('Main', ['PlaysModule',]);

  app.config([ '$routeProvider', function($routeProvider) {
    $routeProvider.when('/', {
      redirectTo: '/today'
    }).when('/home', {
      redirectTo: '/today'
    }).when('/today', {
      templateUrl: 'partials/today.html',
      controller: 'PlaysController',
      controllerAs: 'ctrl'
    }).when('/soon', {
      templateUrl: 'partials/soon.html',
      controller: 'PlaysController',
      controllerAs: 'ctrl'
    }).when('/information', {
      templateUrl: 'partials/informations.html',
      controller: '', //controller per informations
      controllerAs: '' //alias controller per informstions
    }).otherwise({
      redirectTo: '/error'
    });
  } ]);


})();
