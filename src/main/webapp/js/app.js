$(document).ready(function () {

    // init the mobile menu sidenav
    $(".button-collapse").sideNav();

    // init the dropdown selectors
    $('select').material_select();

    // apre i modal di materialize
    $('.modal-trigger').leanModal({
        dismissible: true, // Modal can be dismissed by clicking outside of the modal
        opacity: .5, // Opacity of modal background
        in_duration: 400, // Transition in duration
        out_duration: 300 // Transition out duration
    });
});

/* può tornare utile
 function close_Sidediv() {
 $('.side-div').removeClass('side-div-w');
 $('.side-div').find('.side-nav-element').addClass('ng-hide');
 }*/

(function () {
    'use strict';

    angular.module('cineweb', ['ngRoute', 'uiGmapgoogle-maps', 'cartModule', 'PlaysModule',
        'tabmodule', 'loginModule', 'registrationModule', 'meModule', 'adminUsers'])

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
            }).when('/info', {
                templateUrl: '../partials/info.html'
            }).when('/registration', {
                templateUrl: '../partials/registration.html',
                controller: 'RegistrationCtrl',
                controllerAs: 'ctrl'
            }).when('/me', {
                templateUrl: '../partials/me.html',
                controller: 'MeController',
                controllerAs: 'ctrl'
            }).when('/login', {
                templateUrl: '../partials/login.html',
                controller: 'LoginController',
                controllerAs: 'c'
            }).when('/admin/users', {
                templateUrl: '../partials/admin/users.html',
                controller: 'AdminUsersController',
                controllerAs: 'ctrl'
            }).when('/error', {
                templateUrl: '../partials/error.html'
            }).otherwise({
                redirectTo: '/error'
            });
        }])

        .config(['uiGmapGoogleMapApiProvider', function (uiGmapGoogleMapApiProvider) {
            uiGmapGoogleMapApiProvider.configure({
                //    key: 'your api key',
                v: '3.17',
                libraries: 'weather,geometry,visualization'
            });
        }])

        .directive('loading', function() {
           return {
               restrict: 'E',
               templateUrl: '../partials/directives/loading.html',
               scope: {
                   loading: '=loading'
               }
           }
        });

})();
