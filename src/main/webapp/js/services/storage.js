(function () {
    'use strict';

    angular.module('storageModule', ['angular-locker'])
        .factory('StorageService', ['locker', function (locker) {

            var CART = 'cart';

            return {

                saveCart: function (cart) {
                    locker.put(CART, JSON.stringify(cart));
                },

                cleanCart: function () {
                    locker.forget(CART);
                },

                loadCart: function () {
                    var cart = "";
                    cart = locker.get(CART);
                    if (cart == "" || cart == "[]" || cart == null || cart == undefined) {
                        return JSON.parse(null);
                    } else {
                        return JSON.parse(cart);
                    }
                }
            }
        }]);

})();