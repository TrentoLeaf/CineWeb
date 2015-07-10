(function () {
    'use strict';

    /* servizio per il salvataggio in LocalStorage del carrello dell'utente */
    angular.module('storageModule', ['angular-locker'])
        .factory('StorageService', ['locker', function (locker) {
            var CART = 'cart'; // chiave della coppia chiave-valore in LocalStorage

            return {

                saveCart: function (cart) {
                    locker.put(CART, JSON.stringify(cart));
                },

                cleanCart: function () {
                    locker.forget(CART);
                },

                loadCart: function () {
                    var cart = locker.get(CART, null);
                    return JSON.parse(cart);
                }
            }
        }]);

})();