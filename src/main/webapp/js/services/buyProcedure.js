(function () {
    'use strict';

    /* modulo per la comunicazione con il server dei dati relativi alla procedura d'acquisto */
    angular.module('buyProcedureModule', ['constantsModule'])

        .factory('BuyProcedure', ['BASE', '$http', '$q', '$log', function (BASE, $http, $q, $log) {
            var BASE_PROCEED = BASE + "/buy/proceed";
            var BASE_PAY = BASE + "/buy/pay";
            return {

                /* richiesta al server per la validazione del carrello
                 * (validazione spettacoli esistenti, numero biglietti acquistabili)
                 */
                proceed: function (cart) {
                    return $http.post(BASE_PROCEED, {cart: cart})
                        .success(function (data) {
                            $log.info('BUY PROCEDURE OK: ' + data);
                            $log.info(data);
                        })
                        .error(function (data, status) {
                            $log.warn('BUY PROCEDURE FAIL: ' + status + " " + data.error);
                        });
                },

                /* invio posti richiesti e dati per il pagamento */
                pay: function (creditCard, cart) {
                    return $http.post(BASE_PAY, {
                        creditCard: creditCard,
                        cart: cart
                    })
                        .success(function (data) {
                            $log.info('BUY PAY OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('BUY PAY FAIL: ' + status + " " + data);
                        });
                }

            }
        }]);

})();