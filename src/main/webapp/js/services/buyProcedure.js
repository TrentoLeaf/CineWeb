(function () {
    'use strict';

    /* modulo per la comunicazione con il server dei dati relativi alla procedura d'acquisto */
    angular.module('buyProcedureModule', ['constantsModule'])

        .factory('BuyProcedure', ['BASE', '$http', function (BASE, $http) {
            var BASE_PROCEED = BASE + "/buy/proceed";
            var BASE_PAY = BASE + "/buy/pay";
            return {

                /* richiesta al server per la validazione del carrello
                 * (validazione spettacoli esistenti, numero biglietti acquistabili)
                 */
                proceed: function (cart) {
                    return $http.post(BASE_PROCEED, {cart: cart});
                },

                /* invio posti richiesti e dati per il pagamento */
                pay: function (creditCard, cart) {
                    return $http.post(BASE_PAY, {
                        creditCard: creditCard,
                        cart: cart
                    });
                }

            }
        }]);

})();