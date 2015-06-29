/**
 * Created by Willo on 29/06/2015.
 */
(function () {
    'use strict';

    angular.module('statsModule', ['constantsModule', 'ngResource'])

        .factory('Stats', ['BASE', '$http', '$q', '$log', function (BASE, $http, $q, $log) {
            var BASE_USERS = BASE + "/users";
            var BASE_FILMS = BASE + "/films";

            return {
                topUsers: function () {
                    return $http.get(BASE_USERS + '/top')
                        .success(function (data) {
                            $log.info('TOP USERS OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('TOP USERS NOT RETRIVED: ' + status + " " + data.error);
                        });
                },

                grossingFilms: function () {
                    return $http.get(BASE_FILMS + '/grossing')
                        .success(function (data) {
                            $log.info('GROSSING FILMS OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('GROSSING FILMS NOT RETRIVED: ' + status + " " + data);
                        });
                }
            }
        }]);

})();