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

    var app = angular.module('cineweb', ['PlaysModule', 'CartModule', 'ngRoute', 'tabmodule', 'loginModule', 'registrationModule', 'usermodule', 'adminUsers']);

    app.config(['$routeProvider', function ($routeProvider) {
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
        }).when('/admin/users', {
            templateUrl: '../partials/admin/users.html',
            controller: 'AdminUsersController',
            controllerAs: 'ctrl'
        }).when('/error', {
            templateUrl: '../partials/error.html'
        }).otherwise({
            redirectTo: '/error'
        });
    }]);
})();
