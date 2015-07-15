(function () {
    'use strict';

    /* modulo per la gestione dei prezzi */
    angular.module('adminPrices', ['pricesModule'])
        .controller('AdminPricesController', ['$location', '$rootScope', 'Prices', function ($location, $rootScope, Prices) {

            var ctrl = this;
            this.error_msg = "";
            this.newPrice = {};
            this.tmpPrice = {};
            this.prices = [];


            // recupera i prezzi del server
            this.loadPrices = function () {
                console.log(Prices);
                Prices.getList().then(function (result) {
                    ctrl.prices = result.data;

                });
            };

            // elimina una tipologia di prezzo
            this.deletePrice = function () {

                // better BEFORE calling this function
                Prices.delete(ctrl.tmpPrice).then( function () {
                    // ok
                    ctrl.loadPrices();
                    console.log("Price deletion success");
                }, function () {
                    // fail
                });
            };


            // aggiunge un nuovo prezzo - tipologia
            this.addPrice = function () {
                Prices.save(ctrl.newPrice).then(function () {
                    ctrl.newPrice = {};
                    ctrl.loadPrices();
                }, function () {
                    // errors
                });
            };

            // apre modal conferma eliminazione
            this.open_delete_modal = function (data) {
                ctrl.tmpPrice = data;
                $('#modal_price_delete').openModal();
            };

            // chiude modal conferma eliminazione
            this.close_delete_modal = function() {
                $('#modal_price_delete').closeModal();
            };

            // apre modal modifica
            this.open_edit_modal = function (data) {
                ctrl.tmpPrice = $rootScope.cloneObject(data);
                $('#modal_price_edit').openModal();
            };

            // chiude modal modifica
            this.close_edit_modal = function() {
                $('#modal_price_edit').closeModal();
            };

            // invia richiesta modifica prezzo
            this.editPrice = function (price) {

                ctrl.error_msg = "";

                Prices.update(price).then(function (data) {
                    // ok
                    ctrl.tmpPrice = {};
                    ctrl.loadPrices();
                    ctrl.close_edit_modal();

                    console.log("UPDATE OK ->");
                    console.log(data);
                }, function () {
                    // fail...
                    ctrl.error_msg = "Modifica fallita."
                    console.log("UPDATE fail");
                });
            };

            this.loadPrices();

        }]);

})();
