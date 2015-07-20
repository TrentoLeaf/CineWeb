(function () {
    'use strict';

    /* modulo per la comunicazione con il server dei dati relativi ai prezzi */
    angular.module('pricesModule', ['constantsModule', 'ngResource'])
        .factory('Prices', ['BASE', '$http', function (BASE, $http) {
            var PRICES_BASE = BASE + "/prices/";

            return {

                /* salva un nuovo prezzo */
                save: function(price) {
                    return $http.post(PRICES_BASE, price);
                },

                /* aggiorna un prezzo esistente */
                update: function (price) {
                    return $http.put(PRICES_BASE+price.type, price);
                },

                /* elimina un prezzo esistente */
                delete: function (price) {
                    return $http.delete(PRICES_BASE+price.type);
                },

                /* recupera la lista di tutti i prezzi esistenti */
                getList: function () {
                    return $http.get(PRICES_BASE);
                }
            }
        }]);
})();