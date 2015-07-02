(function () {
    'use strict';

    angular.module('pricesModule', ['constantsModule', 'ngResource'])
        .factory('Prices', ['BASE', '$http', '$log', function (BASE, $http, $log) {
            var PRICES_BASE = BASE + "/prices/";

            return {

                save: function(price) {
                    return $http.post(PRICES_BASE, price)
                        .success(function() {
                            $log.info("SAVE success price " + JSON.stringify(price));
                        })
                        .error(function(data, status) {
                            $log.warn("SAVE failed price " + JSON.stringify(price)
                            + " --> " + status + ", " + data);
                        });
                },

                update: function (price) {
                    return $http.put(PRICES_BASE+price.type, price)
                        .success( function () {
                            $log.info("UPDATE success on price" + JSON.stringify(price));
                        }
                    )
                        .error(function (data, status) {
                            $log.warn("UPDATE fail on price"  + JSON.stringify(price)
                            + " --> " + status + ", " +data);
                        });
                },

                delete: function (price) {
                    return $http.delete(PRICES_BASE+price.type)
                        .success (function () {
                        $log.info("DELETION success on price" + JSON.stringify(price));
                    })
                        .error(function (data, status) {
                            $log.warn("DELETION fail on price" + JSON.stringify(price)
                            + " --> " + status + ", " + data);
                        });
                },

                getList: function () {
                    return $http.get(PRICES_BASE)
                        .success (function () {
                        $log.info("GET PRICES success");
                    })
                        .error(function (data, status) {
                            $log.warn("GET PRICES fail"
                                + " --> " + status + ", " + data);
                        });
                }

            }
        }]);
})();