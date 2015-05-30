(function () {
    'use strict';

    angular.module('usersModule', ['constantsModule', 'ngResource'])

        .factory('Users', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/users/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        .factory('Auth', ['BASE', '$http', '$q', '$log', function (BASE, $http, $q, $log) {
            var BASE_USERS = BASE + "/users";

            return {
                login: function (email, password) {
                    var deferred = $q.defer();

                    $http.post(BASE_USERS + '/login', {email: email, password: password})
                        .success(function (data) {
                            deferred.resolve(data);
                        })
                        .error(function (data) {
                            $log.warn('LOGIN FAILED: ' + status + " " + data.error);
                            deferred.reject(data.error);
                        });

                    return deferred.promise;
                },

                logout: function () {
                    return $http.post(BASE_USERS + '/logout')
                        .error(function (data, status) {
                            $log.warn('LOGOUT FAILED: ' + status + " " + data);
                        });
                },

                changePassword: function (email, oldPassword, newPassword) {
                    return $http.post(BASE_USERS + '/change-password', {
                        email: email,
                        oldPassword: oldPassword,
                        newPassword: newPassword
                    });
                }
            }
        }]);

})();