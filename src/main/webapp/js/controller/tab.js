/**
 * Created by stefano on 30/05/15.
 */
(function() {
    'use strict';

    var app=angular.module('TabModule', []).controller('TabController', function() {

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