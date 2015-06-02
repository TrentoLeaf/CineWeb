(function () {
    'use strict';

    angular.module('tabmodule', [])
        .controller('TabController', function () {

            this.tab = -1;

            this.setTab = function (tab) {
                if (this.tab == tab) {
                    // hide side-div
                    $('.side-div').removeClass('side-div-w');
                    this.tab = -1;
                } else if (tab == -1) {
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