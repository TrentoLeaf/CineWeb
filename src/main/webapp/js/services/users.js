(function () {
    'use strict';

    /* modulo per la comunicazione con il server dei dati relativi all'utente */
    angular.module('usersModule', ['constantsModule', 'ngResource'])

        .factory('Users', ['BASE', '$resource', function (BASE, $resource) {
            return $resource(BASE + '/users/:id', {id: '@id'}, {
                update: {
                    method: 'PUT'
                }
            });
        }])

        .factory('Auth', ['BASE', '$http', '$q', '$templateCache', function (BASE, $http, $q, $templateCache) {
            var BASE_USERS = BASE + "/users";
            var BASE_BOOKINGS = BASE + "/bookings";
            var BASE_TICKETS = BASE + "/tickets";

            return {
                /* invio dati di login */
                login: function (email, password) {
                    var deferred = $q.defer();
                    $http.post(BASE_USERS + '/login', {email: email, password: password})
                        .success(function (data) {
                            deferred.resolve(data);
                        })
                        .error(function (data, status) {
                            deferred.reject(data);
                        });
                    return deferred.promise;
                },

                /* invio richiesta di logout */
                logout: function () {
                    return $http.post(BASE_USERS + '/logout')
                        .success(function (data) {
                            $templateCache.removeAll();
                        })
                        .error(function (data, status) {
                        });
                },

                /* richiesta normale cambio password */
                changePassword: function (email, oldPassword, newPassword) {
                    return $http.post(BASE_USERS + '/change-password', {
                        email: email,
                        oldPassword: oldPassword,
                        newPassword: newPassword
                    });
                },

                /* richiesta cambio password in seguito alla richiesta di recupero della password */
                changePasswordWithCode: function (email, code, newPassword) {
                    return $http.post(BASE_USERS + '/change-password-code', {
                        email: email,
                        code: code,
                        newPassword: newPassword
                    });
                },

                /* richiesta di registrazione */
                registration: function (email, password, firstName, secondName) {
                    return $http.post(BASE_USERS + '/registration', {
                        email: email,
                        password: password,
                        firstName: firstName,
                        secondName: secondName
                    });
                },

                /* invia la conferma del click sull'url di registrazione */
                confirmRegistration: function (code) {
                    return $http.post(BASE_USERS + '/confirm', {code: code});
                },

                /* richiesta recupero password */
                forgotPassword: function (email) {
                    return $http.post(BASE_USERS + "/forgot-password", {email: email});
                },

                /* richiesta dati di base dell'utente. Ritorna una promise della richiesta*/
                me: function () {
                    var deferred = $q.defer();
                    $http.get(BASE_USERS + "/me")
                        .success(function (data) {
                            deferred.resolve(data);
                        })
                        .error(function (data, status) {
                            deferred.reject(data);
                        });
                    return deferred.promise;
                },

                /* richiesta cronologia acquisti dell'utente */
                my_bookings: function () {
                    return $http.get(BASE_BOOKINGS + "/my");
                },

                /* richiesta (admin) cronologia acquisti di un'utente*/
                user_bookings: function (uid) {
                    return $http.get(BASE_BOOKINGS + "/" + uid);
                },

                /* richiesta (admin) cancellazione biglietto */
                deleteTicket: function (tid) {
                    return $http.delete(BASE_TICKETS + "/" + tid);
                }
            }
        }]);

})();