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
            this.tmpPrice = {};

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

            /*
            this.addPrice = function () {
                this.newPrice.$save(function (data) {
                    ctrl.price.push(data);
                    ctrl.newPrice = new Prices();
                }, function () {
                    // errors
                });
            };
            */

            this.open_delete_modal = function (ticket) {
                $('#modal_deleteAgree').openModal();
                this.tmpPrice = ticket;
            };

            /*
          //TODO: edit a given price
            this.editPrice = function (price) {
                price. = Math.random().toString(36).substring(7);
                Prices.update({type: prices}, price).$promise.then(function (data) {
                    // ok
                    console.log("UPDATE OK ->");
                    console.log(data);
                }, function () {
                    // fail...
                    console.log("UPDATE fail");
                });
            };
*/

        }]);

})();
