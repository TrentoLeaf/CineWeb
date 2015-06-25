(function () {
    'use strict';

    angular.module('adminPrices', ['pricesModule'])
        .controller('AdminPricesController', ['$location', '$rootScope', 'Prices', '$resource', function ($location, Prices, $rootScope, $resource) {

            var Test = $resource('api/prices/:type', {type: '@type'}, {
                update: {
                    method: 'PUT'
                }
            });

            var ctrl = this;
            this.newPrice = new Test();
            this.tmpPrice = new Test();

            console.log(this.newPrice);
            // remove a priceClass
            this.deletePrice = function () {

                console.log(Test);
                // better BEFORE calling this function
                Test.delete({type: ctrl.tmpPrice.type}, function () {
                    // ok
                    console.log("Price deletion success");
                }, function () {
                    // fail
                });
            };


            this.addPrice = function () {
                ctrl.newPrice.$save(function () {

                    this.newPrice = new Test();
                }, function () {
                    // errors
                });
            };


            this.open_delete_modal = function (data) {
                ctrl.tmpPrice = data;
                $('#modal_deleteAgree').openModal();
            };

            /*
            * Problema durante la modifica, i valori cambiano anche nella tabella ed è necessario fare un refresh
            */
            this.open_edit_modal = function (data) {

                ctrl.tmpPrice = data;
                $('#modal_edit').openModal();
            };

            this.editPrice = function (price) {

                Test.update({type: price.type}, price).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };

        }]);

})();
