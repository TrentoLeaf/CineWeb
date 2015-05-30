$(document).ready(function(){
    // init the mobile menu sidenav
    $(".button-collapse").sideNav();

    // intercept all clicks
    $('main').on('click.hideSide',function(e) {
        // check if click come from hide on mobile buttons
        if ($(e.target).hasClass('hide-side-nav-button-mobile')) {
            $('.side-div').removeClass('side-div-w');
            $('.side-div').find('li').addClass('ng-hide');
        }

        if (!$(e.target).hasClass('side-div')) {
            // check if click not come from the Sidediv
            if (!($(e.target).parents().hasClass('side-div'))) {
                // hide the side-div
                $('.side-div').removeClass('side-div-w');
                $('.side-div').find('li').addClass('ng-hide');
            }
        }
    });

});
(function() {
    'use strict';

    var app= angular.module('Main', ['PlaysModule']);
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



})();
