(function () {
    'use strict';

    /* modulo per la gestione della barra laterale carrello e login */
    angular.module('tabmodule', [])
        .controller('TabController', function () {

            this.tab = -1;

            this.setTab = function (tab) {
                if (this.tab == tab) { // chiusura al secondo click sulla stessa icona navbar
                    // hide side-div
                    $('.side-div').removeClass('side-div-w');
                    this.tab = -1;
                } else if (tab == -1) { // chiusura per click generico al di fuori della barra o su un bottone
                    // hide side-div
                    $('.side-div').removeClass('side-div-w');
                    this.tab = -1;
                } else {    // apertura carrello o login
                    // show side-div
                    $('.side-div').addClass('side-div-w');
                    this.tab = tab;
                }
            };

            // funzione per la chiusura della barra
            this.closeTab = function () {
                this.setTab(-1);
            };

            // setta il div da mostrare carrello/login
            this.isSet = function (Value) {
                return this.tab === Value;
            };
        });

})();