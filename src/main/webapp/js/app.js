$(document).ready(function(){
    // init the mobile menu sidenav
    $(".button-collapse").sideNav();

    // init the dropdown selectors
        $('select').material_select();


    // intercept all clicks
    $('main').on('click.hideSide',function(e) {
        // check if click come from hide on mobile buttons
        if ($(e.target).hasClass('hide-side-nav-button-mobile')) {
            $('.side-div').removeClass('side-div-w');
            $('.side-div').find('.side-nav-element').addClass('ng-hide');
        }

        if (!$(e.target).hasClass('side-div')) {
            // check if click not come from the Sidediv
            if (!($(e.target).parents().hasClass('side-div'))) {
                // hide the side-div
                $('.side-div').removeClass('side-div-w');
                $('.side-div').find('.side-nav-element').addClass('ng-hide');
            }
        }
    });

});
(function() {
    'use strict';

    var app= angular.module('cineweb', ['ngRoute', 'PlaysModule']);

    // handles routes
    app.config([ '$routeProvider', function($routeProvider) {
        $routeProvider.when('/', {
            redirectTo: '/today'
        }).when ('/home', {
            redirectTo: '/today'
        }).when('/today', {
            templateUrl: 'partials/today.html'
        }).when('/soon', {
            templateUrl: 'partials/soon.html'
        }).when('/informations', {
            templateUrl: 'partials/informations.html'
        }).when('/registration', {
            templateUrl: 'partials/registration.html',
            controller: 'registrationCtrl',
            controllerAs: 'ctrl'
        }).when('/error', {
            templateUrl: 'partials/error.html'
        }).otherwise({
            redirectTo: '/error'
        });
    } ]);


    app.controller('TabController', function() {

        this.tab = -1;

        this.setTab = function (tab) {

            if (this.tab == tab ) {
                // hide side-div
                $('.side-div').removeClass('side-div-w');
                this.tab = -1;
            } else {

                // show side-div
                $('.side-div').addClass('side-div-w');

                this.tab = tab;
            }

        };

        this.isSet = function (Value) {
            return this.tab === Value;
        };
    });

    app.controller('registrationCtrl', function () {

        this.user = "";

        this.submit = function () {
            console.log(this.user);
            // TODO! call backend
        };

    });



})();
