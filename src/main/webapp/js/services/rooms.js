(function () {
    'use strict';

    /* modulo per la comunicazione con il server dei dati relativi alle sale */
    angular.module('roomsModule', ['constantsModule', 'ngResource'])

        /* generic http methods to add a room */
        .factory('Theatre', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/rooms', {
                addRoom: {
                    method: 'POST'
                }
            });
        }])

        .factory('Rooms', ['BASE', '$http', function (BASE, $http) {
            var BASE_ROOMS = BASE + "/rooms";
            var BASE_PLAYS = BASE + "/plays";

            return {
                /* recupera l'elenco delle sale (senza mappa) */
                getRoomsOnly: function () {
                    return $http.get(BASE_ROOMS);
                },

                /* recupera la mappa di una sala con i posti modificabili */
                getRoomEditableByID: function (id) {
                    return $http.get(BASE_ROOMS + '/' + id);
                },

                /* recupera la mappa di una sala con i posti migliori */
                getRoomTopByID: function (id) {
                    return $http.get(BASE_ROOMS + '/' + id + '/top');
                },

                /* recupera la mappa e lo stato di una sala relativa ad uno spettacolo */
                getRoomStatus: function (id) {
                    return $http.get(BASE_PLAYS + '/' + id + '/room');
                },

                /* invia la mappa di una sala modificata */
                editRoom: function (id, obj) {
                    return $http.put(BASE_ROOMS + '/' + id, obj);
                },

                /* elimina una sala esistente solo se tutti i posti sono liberi */
                delete: function (rid) {
                    return $http.delete(BASE_ROOMS + '/' + rid);
                }
            }
        }]);

})();