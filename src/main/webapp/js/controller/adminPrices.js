(function () {
    'use strict';

    angular.module('adminPrices', ['pricesModule'])
        .controller('AdminPricesController', ['$location', '$rootScope', 'Prices', '$resource', function ($location, Prices, $rootScope, $resource) {

            var Test = $resource('api/prices/:id', {type: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });

            var ctrl = this;
            this.newPrice = new Test();
            this.tmpPrice = new Test();

            console.log(this.newPrice);

            var init = function () {
                if ($rootScope.isUserLogged == false) {
                    $rootScope.afterLogin = "userArea";
                    $location.path('/login');
                }
            };

            // remove a priceClass
            this.deletePrice = function () {

                console.log(Test);
                // better BEFORE calling this function
                Test.delete({type: ctrl.tmpPrice.type}, function () {
                    // ok
                    ctrl.updatePrices();
                    console.log("Price deletion success");
                }, function () {
                    // fail
                });
            };


            this.addPrice = function () {
                ctrl.newPrice.$save(function () {
                    ctrl.newPrice = new Test();
                    ctrl.updatePrices();
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
                $('#modal_price_edit').openModal();
            };

            this.close_edit_modal = function() {
                $('#modal_price_edit').closeModal();
            };


            this.editPrice = function (price) {

                Test.update({type: price.type}, price).$promise.then(function (data) {
                    // ok
                    ctrl.updatePrices();
                    console.log("UPDATE OK ->");
                    console.log(data);
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };


            this.updatePrices = function () {
                /* //TODO non funziona --> Prices.query is not a function
                 Prices.query(function (data) {
                 $rootScope.tickets = data;
                 });
                 */
            };

            init();

        }]);

})();
