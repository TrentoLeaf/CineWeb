(function () {
    'use strict';

    angular.module('roomsModule', ['constantsModule', 'ngResource'])

        .factory('Theatre', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/rooms', {
                addRoom: {
                    method: 'POST'
                }
            });
        }])

        .factory('Rooms', ['BASE', '$http', '$q', '$log', function (BASE, $http, $q, $log) {
            var BASE_ROOMS = BASE + "/rooms";
            var BASE_PLAYS = BASE + "/plays";

            return {
                getRoomsOnly: function () {
                    return $http.get(BASE_ROOMS)
                        .success(function (data) {
                            $log.info('GET ROOMS OK: ' + data);
                            $log.info(data);
                        })
                        .error(function (data, status) {
                            $log.warn('GET ROOMS FAILED: ' + status + " " + data.error);
                        });
                },

                getRoomEditableByID: function (id) {
                    return $http.get(BASE_ROOMS + '/' + id)
                        .success(function (data) {
                            $log.info('GET ROOM OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('GET ROOM FAILED: ' + status + " " + data);
                        });
                },

                getRoomTopByID: function (id) {
                    return $http.get(BASE_ROOMS + '/' + id + '/top')
                        .success(function (data) {
                            $log.info('GET ROOM top OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('GET ROOM top FAILED: ' + status + " " + data);
                        });
                },

                getRoomStatus: function (id) {
                    return $http.get(BASE_PLAYS + '/' + id + '/room')
                        .success(function (data) {
                            $log.info('GET ROOM STATUS OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('GET ROOM STATUS FAILED: ' + status + " " + data);
                        });
                },

                editRoom: function (id, obj) {
                    return $http.put(BASE_ROOMS + '/' + id, obj)
                        .success(function (data) {
                            $log.info('EDIT ROOM OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('EDIT ROOM FAILED: ' + status + " " + data);
                        });
                }
            }
        }]);

})();