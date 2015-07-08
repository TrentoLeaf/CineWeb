(function () {
    'use strict';

    angular.module('adminPrices', ['pricesModule'])
        .controller('AdminPricesController', ['$location', '$rootScope', 'Prices', function ($location, $rootScope, Prices) {

            var ctrl = this;
            this.newPrice = {};
            this.tmpPrice = {};
            this.prices = [];

            console.log(this.newPrice);

            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }
            };

            this.loadPrices = function () {
                console.log(Prices);
                Prices.getList().then(function (result) {
                    ctrl.prices = result.data;

                });
            };

            // remove a priceClass
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


            this.addPrice = function () {
                Prices.save(ctrl.newPrice).then(function () {
                    ctrl.newPrice = {};
                    ctrl.loadPrices();
                }, function () {
                    // errors
                });
            };


            this.open_delete_modal = function (data) {
                ctrl.tmpPrice = data;
                $('#modal_price_delete').openModal();
            };

            this.close_delete_modal = function() {
                $('#modal_price_delete').closeModal();
            };

            /*
             * Problema durante la modifica, i valori cambiano anche nella tabella ed è necessario fare un refresh
             */
            this.open_edit_modal = function (data) {
                ctrl.tmpPrice = data;
                ctrl.tmpPrice.id =
                $('#modal_price_edit').openModal();
            };

            this.close_edit_modal = function() {
                $('#modal_price_edit').closeModal();
            };


            this.editPrice = function (price) {

                Prices.update(price).then(function (data) {
                    // ok
                    ctrl.loadPrices();
                    console.log("UPDATE OK ->");
                    console.log(data);
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };

            init();
            this.loadPrices();

        }]);

})();
