(function () {
    'use strict';

    angular.module('buyProcedureModule', ['constantsModule'])

        .factory('BuyProcedure', ['BASE', '$http', '$q', '$log', function (BASE, $http, $q, $log) {
            var BASE_PROCEED = BASE + "/buy/proceed";
            var BASE_PAY = BASE + "/buy/pay";
            return {

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

                pay: function (data_to_server) {
                    return $http.post(BASE_PAY, data_to_server)
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