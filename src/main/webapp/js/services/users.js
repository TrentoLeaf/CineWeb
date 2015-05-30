(function () {
    'use strict';

    angular.module('usersModule', ['constants'])

        .factory('Users', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/users/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        .factory('Auth', ['BASE', $http, $q, $log, function (BASE, $http, $q, $log) {
            return {
                login: function (email, password) {
                    var deferred = $q.defer();

                    $http.post(BASE + '/login', {email: email, password: password})
                        .success(function (data) {
                            deferred.resolve(data);
                        })
                        .error(function () {
                            $log.warn('LOGIN FAILED: ' + status + " " + data.error);
                            deferred.reject(data.error);
                        });

                    return deferred.promise;
                },

                logout: function () {
                    $http.post(BASE + '/logout')
                        .error(function (data, status) {
                            $log.warn('LOGOUT FAILED: ' + status + " " + data);
                        });
                },

                changePassword: function (email, oldPassword, newPassword) {
                    return $http.post(BASE + 'change-password', {
                        email: email,
                        oldPassword: oldPassword,
                        newPassword: newPassword
                    });
                }
            }
        }]);

})();