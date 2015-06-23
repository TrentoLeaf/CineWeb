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
                    var cart = locker.get(CART);
                    return (cart != undefined) ? JSON.parse(cart) : [];
                }
            }
        }]);

})();