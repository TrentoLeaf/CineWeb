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

            // store the status of the current user
            var status = null;

            return {
                login: function (email, password) {
                    return $http.post(BASE_USERS + '/login', {email: email, password: password})
                        .success(function (data) {
                            $log.info('LOGIN OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('LOGIN FAILED: ' + status + " " + data.error);
                        });
                },

                logout: function () {
                    return $http.post(BASE_USERS + '/logout')
                        .success(function (data) {
                            $log.info('LOGOUT OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('LOGOUT FAILED: ' + status + " " + data);
                        });
                },

                changePassword: function (email, oldPassword, newPassword) {
                    return $http.post(BASE_USERS + '/change-password', {
                        email: email,
                        oldPassword: oldPassword,
                        newPassword: newPassword
                    })
                        .success(function (data) {
                            $log.info('CHANGE PASSWORD OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('CHANGE PASSWORD FAILED: ' + status + " " + data);
                        });
                },

                registration: function (email, password, firstName, secondName) {
                    return $http.post(BASE_USERS + '/registration', {
                        email: email,
                        password: password,
                        firstName: firstName,
                        secondName: secondName
                    })
                        .success(function (data) {
                            $log.info('REGISTRATION OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('REGISTRATION FAILED: ' + status + " " + data);
                        });
                },

                confirmRegistration: function (code) {
                    return $http.post(BASE_USERS + '/confirm', {code: code})
                        .success(function (data) {
                            $log.info('CONFIRM REGISTRATION OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('CONFIRM REGISTRATION FAILED: ' + status + " " + data);
                        });
                },

                forgotPassword: function (email) {
                    return $http.post(BASE_USERS + "/forgot-password", {email: email})
                        .success(function (data) {
                            $log.info('FORGOT PASSWORD OK: ' + data);
                        })
                        .error(function (data, status) {
                            $log.warn('FORGOT PASSWORD FAILED: ' + status + " " + data);
                        });
                },

                me: function () {
                    var deferred = $q.defer();
                    if (status) {
                        deferred.resolve(status);
                    } else {
                        $http.get(BASE_USERS + "/me")
                            .success(function (data) {
                                status = data;
                                $log.info('ME OK: ' + data);
                                deferred.resolve(status);
                            })
                            .error(function (data, status) {
                                $log.warn('ME FAILED: ' + status + " " + data);
                                deferred.reject(status);
                            });
                    }
                    return deferred.promise;
                }
            }
        }]);

})();