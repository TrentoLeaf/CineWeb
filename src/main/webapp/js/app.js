(function () {
    'use strict';

    angular.module('cineweb', ['PlaysModule', 'CartModule', 'ngRoute', 'tabmodule', 'loginModule', 'registrationModule', 'usermodule'])
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider.when('/', {
                redirectTo: '/today'
            }).when('/home', {
                redirectTo: '/today'
            }).when('/today', {
                templateUrl: '../partials/today.html',
                controller: 'PlaysController',
                controllerAs: 'ctrl'
            }).when('/soon', {
                templateUrl: '../partials/soon.html',
                controller: 'PlaysController',
                controllerAs: 'ctrl'
            }).when('/informations', {
                templateUrl: '../partials/informations.html',
                controller: '', //controller per informations
                controllerAs: '' //alias controller per informstions
            }).when('/registration', {
                templateUrl: '../partials/registration.html',
                controller: 'RegistrationCtrl',
                controllerAs: 'ctrl'
            }).when('/me', {
                templateUrl: '../partials/userArea.html',
                controller: 'UserController',
                controllerAs: 'ctrl'
            }).when('/error', {
                templateUrl: '../partials/error.html'
            }).otherwise({
                redirectTo: '/error'
            });
        }]);

})();
