$(document).ready(function () {

    // init the mobile menu sidenav
    $(".button-collapse").sideNav();

    // init the dropdown selectors
    $('select').material_select();

    // apre i modal di materialize
    $('.modal-trigger').leanModal({         // ANDR REMOVE
        dismissible: true, // Modal can be dismissed by clicking outside of the modal
        opacity: .5, // Opacity of modal background
        in_duration: 400, // Transition in duration
        out_duration: 300, // Transition out duration
        ready: function() { /*alert('Ready');*/ }, // Callback for Modal open
        complete: function() { /*alert('Closed');*/ } // Callback for Modal close
    });


   /* // intercept all clicks
    $('main').on('click.hideSide', function (e) {
        // check if click come from hide on mobile buttons
        if ($(e.target).hasClass('hide-side-nav-button-mobile')) {
            // hide the side-div
            close_Sidediv();
        }

        if (!$(e.target).hasClass('side-div')) {
            // check if click not come from the Sidediv
            if (!($(e.target).parents().hasClass('side-div'))) {
                // hide the side-div
                close_Sidediv();
            }
        }
    });*/

});

function close_Sidediv() {
    $('.side-div').removeClass('side-div-w');
    $('.side-div').find('.side-nav-element').addClass('ng-hide');
}


(function () {
    'use strict';

    var app = angular.module('cineweb', ['PlaysModule', 'CartModule', 'ngRoute', 'tabmodule', 'loginModule', 'registrationModule', 'usermodule']);

    app.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/', {
            redirectTo: '/today'
        }).when('/home', {
            redirectTo: '/today'
        }).when('/today', {
            templateUrl: '../partials/today2.html',
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
