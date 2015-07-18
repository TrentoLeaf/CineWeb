/*Created by Willo on 29/06/2015. */
(function () {
    'use strict';

    angular.module('statsModule', ['constantsModule', 'ngResource'])

        .factory('Stats', ['BASE', '$http', function (BASE, $http) {
            var BASE_USERS = BASE + "/users";
            var BASE_FILMS = BASE + "/films";

            return {
                /* recupera le statistiche degli utenti che hanno speso maggiormente */
                topUsers: function () {
                    return $http.get(BASE_USERS + '/top');
                },

                /* recupera le statistiche dei film che sono più richiesti */
                grossingFilms: function () {
                    return $http.get(BASE_FILMS + '/grossing');
                }
            }
        }]);

})();